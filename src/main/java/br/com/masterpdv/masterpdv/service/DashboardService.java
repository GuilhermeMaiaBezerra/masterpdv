package br.com.masterpdv.masterpdv.service;

import br.com.masterpdv.masterpdv.repository.VendaRepository;
import br.com.masterpdv.masterpdv.repository.VendaItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DashboardService {
    
    @Autowired
    private VendaRepository vendaRepository;
    
    @Autowired
    private VendaItemRepository vendaItemRepository;
    
    private final String[] cores = {"#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0", "#9966FF", "#FF9F40"};
    
    public Map<String, Object> getDadosDashboard() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("=== BUSCANDO DADOS REAIS DO BANCO ===");
            
            Map<String, Object> dashboard = getDashboardData();
            List<Map<String, Object>> graficoPagamentos = getGraficoPagamentos();
            List<Map<String, Object>> vendasSemana = getVendasSemana();
            List<Map<String, Object>> topProdutos = getTopProdutos();
            
            response.put("success", true);
            response.put("dashboard", dashboard);
            response.put("grafico_pagamentos", graficoPagamentos);
            response.put("vendas_semana", vendasSemana);
            response.put("top_produtos", topProdutos);
            
            System.out.println("✅ Dashboard carregado com sucesso!");
            
        } catch (Exception e) {
            System.err.println("❌ ERRO ao buscar dados: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return response;
    }
    
    private Map<String, Object> getDashboardData() {
        Map<String, Object> dashboard = new HashMap<>();
        
        // 1. Total de produtos ativos
        Long totalProdutos = vendaRepository.countProdutosAtivos();
        if (totalProdutos == null) totalProdutos = 0L;
        System.out.println("Total produtos ativos: " + totalProdutos);
        
        // 2. Vendas e faturamento do mês
        Long totalVendas = 0L;
        BigDecimal faturamento = BigDecimal.ZERO;
        
        try {
            List<Object[]> vendasMesList = vendaRepository.getVendasFaturamentoMes();
            if (vendasMesList != null && !vendasMesList.isEmpty()) {
                Object[] result = vendasMesList.get(0);
                if (result != null && result.length > 0) {
                    // Primeiro campo: total de vendas
                    if (result[0] != null) {
                        totalVendas = ((Number) result[0]).longValue();
                    }
                    // Segundo campo: faturamento
                    if (result.length > 1 && result[1] != null) {
                        faturamento = (BigDecimal) result[1];
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar vendas do mês: " + e.getMessage());
        }
        System.out.println("Vendas no mês: " + totalVendas);
        System.out.println("Faturamento no mês: R$ " + faturamento);
        
        // 3. Faturamento mês anterior
        BigDecimal faturamentoAnterior = BigDecimal.ZERO;
        try {
            faturamentoAnterior = vendaRepository.getFaturamentoMesAnterior();
            if (faturamentoAnterior == null) faturamentoAnterior = BigDecimal.ZERO;
        } catch (Exception e) {
            System.err.println("Erro ao buscar faturamento anterior: " + e.getMessage());
        }
        System.out.println("Faturamento mês anterior: R$ " + faturamentoAnterior);
        
        // 4. Calcular crescimento
        BigDecimal crescimento = BigDecimal.ZERO;
        if (faturamentoAnterior.compareTo(BigDecimal.ZERO) > 0) {
            crescimento = faturamento.subtract(faturamentoAnterior)
                .divide(faturamentoAnterior, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        }
        
        // Formatar faturamento para exibição (com ponto milhar e vírgula decimal)
        String faturamentoFormatado = String.format("%,.2f", faturamento).replace(",", "v");
        faturamentoFormatado = faturamentoFormatado.replace(".", ",");
        faturamentoFormatado = faturamentoFormatado.replace("v", ".");
        
        dashboard.put("faturamento", faturamentoFormatado);
        dashboard.put("vendas", totalVendas);
        dashboard.put("produtos", totalProdutos);
        dashboard.put("crescimento", crescimento.setScale(1, RoundingMode.HALF_UP));
        
        return dashboard;
    }
    
    private List<Map<String, Object>> getGraficoPagamentos() {
        List<Map<String, Object>> dados = new ArrayList<>();
        
        try {
            List<Object[]> resultados = vendaRepository.getVendasPorFormaPagamento();
            System.out.println("Registros de pagamentos encontrados: " + (resultados != null ? resultados.size() : 0));
            
            if (resultados != null && !resultados.isEmpty()) {
                int i = 0;
                for (Object[] row : resultados) {
                    if (row != null && row.length >= 3) {
                        String forma = row[0] != null ? row[0].toString() : "Não especificado";
                        Long quantidade = row[1] != null ? ((Number) row[1]).longValue() : 0L;
                        BigDecimal total = row[2] != null ? (BigDecimal) row[2] : BigDecimal.ZERO;
                        
                        Map<String, Object> item = new HashMap<>();
                        item.put("forma", forma);
                        item.put("quantidade", quantidade);
                        item.put("total", total);
                        item.put("cor", cores[i % cores.length]);
                        
                        dados.add(item);
                        System.out.println("  - " + forma + ": " + quantidade + " vendas, R$ " + total);
                        i++;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar gráfico de pagamentos: " + e.getMessage());
        }
        
        return dados;
    }
    
private List<Map<String, Object>> getVendasSemana() {
    List<Map<String, Object>> dados = new ArrayList<>();
    
    try {
        LocalDateTime dataInicio = LocalDate.now().minusDays(6).atStartOfDay();
        List<Object[]> resultados = vendaRepository.getVendasUltimos7Dias(dataInicio);
        System.out.println("Registros de vendas semana encontrados: " + (resultados != null ? resultados.size() : 0));
        
        // Criar mapa para os últimos 7 dias
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        Map<String, Map<String, Object>> mapaDias = new LinkedHashMap<>();
        
        for (int i = 6; i >= 0; i--) {
            LocalDate data = LocalDate.now().minusDays(i);
            String dataStr = data.format(formatter);
            Map<String, Object> dia = new HashMap<>();
            dia.put("data", dataStr);
            dia.put("quantidade", 0L);
            dia.put("total", BigDecimal.ZERO);
            mapaDias.put(dataStr, dia);
        }
        
        // Preencher com dados reais
        if (resultados != null && !resultados.isEmpty()) {
            for (Object[] row : resultados) {
                if (row != null && row.length >= 3) {
                    // CORREÇÃO AQUI - Converter Timestamp corretamente
                    LocalDate data = null;
                    if (row[0] instanceof java.sql.Date) {
                        data = ((java.sql.Date) row[0]).toLocalDate();
                    } else if (row[0] instanceof java.sql.Timestamp) {
                        // Timestamp - converte para LocalDate
                        java.sql.Timestamp ts = (java.sql.Timestamp) row[0];
                        data = ts.toLocalDateTime().toLocalDate();
                    } else if (row[0] instanceof java.time.LocalDateTime) {
                        data = ((java.time.LocalDateTime) row[0]).toLocalDate();
                    } else if (row[0] instanceof java.time.LocalDate) {
                        data = (java.time.LocalDate) row[0];
                    } else if (row[0] instanceof java.util.Date) {
                        // Para java.util.Date
                        java.util.Date date = (java.util.Date) row[0];
                        data = new java.sql.Date(date.getTime()).toLocalDate();
                    }
                    
                    if (data != null) {
                        String dataStr = data.format(formatter);
                        Long quantidade = row[1] != null ? ((Number) row[1]).longValue() : 0L;
                        BigDecimal total = row[2] != null ? (BigDecimal) row[2] : BigDecimal.ZERO;
                        
                        if (mapaDias.containsKey(dataStr)) {
                            mapaDias.get(dataStr).put("quantidade", quantidade);
                            mapaDias.get(dataStr).put("total", total);
                        }
                    }
                }
            }
        }
        
        dados.addAll(mapaDias.values());
        
    } catch (Exception e) {
        System.err.println("Erro ao buscar vendas da semana: " + e.getMessage());
        e.printStackTrace();
    }
    
    return dados;
}
    
    private List<Map<String, Object>> getTopProdutos() {
        List<Map<String, Object>> dados = new ArrayList<>();
        
        try {
            List<Object[]> resultados = vendaItemRepository.findTopProdutosMes();
            System.out.println("Top produtos encontrados: " + (resultados != null ? resultados.size() : 0));
            
            if (resultados != null && !resultados.isEmpty()) {
                for (Object[] row : resultados) {
                    if (row != null && row.length >= 3) {
                        String nome = row[0] != null ? row[0].toString() : "Produto";
                        Long quantidade = row[1] != null ? ((Number) row[1]).longValue() : 0L;
                        BigDecimal faturamento = row[2] != null ? (BigDecimal) row[2] : BigDecimal.ZERO;
                        
                        Map<String, Object> produto = new HashMap<>();
                        produto.put("nome", nome);
                        produto.put("quantidade", quantidade);
                        produto.put("faturamento", faturamento);
                        
                        dados.add(produto);
                        System.out.println("  - " + nome + ": " + quantidade + " unidades, R$ " + faturamento);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar top produtos: " + e.getMessage());
            e.printStackTrace();
        }
        
        return dados;
    }
}