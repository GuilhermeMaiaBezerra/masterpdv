package br.com.masterpdv.masterpdv.service;

import br.com.masterpdv.masterpdv.entity.*;
import br.com.masterpdv.masterpdv.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class VendaService {
    
    @Autowired
    private ProdutoRepository produtoRepository;
    
    @Autowired
    private VendaRepository vendaRepository;
    
    @Autowired
    private VendaItemRepository vendaItemRepository;
    
    @Autowired
    private CaixaRepository caixaRepository;
    
    /**
     * Listar produtos para o PDV (com estoque > 0 e ativos)
     */
    public Map<String, Object> listarProdutosPDV(String busca) {
        System.out.println("📦 listarProdutosPDV - Buscando produtos do banco de dados!");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Usar o método existente no repository
            List<Produto> produtos = produtoRepository.findProdutosAtivosWithBusca(busca);
            
            List<Map<String, Object>> produtosList = new ArrayList<>();
            
            for (Produto p : produtos) {
                // Verificar se tem estoque (considerar null como 0)
                Integer estoque = p.getQuantidadeEstoque() != null ? p.getQuantidadeEstoque() : 0;
                
                // Só mostrar produtos com estoque > 0
                if (estoque <= 0) {
                    continue;
                }
                
                Map<String, Object> prod = new HashMap<>();
                prod.put("id_produto", p.getId());
                prod.put("nome", p.getNome());
                prod.put("descricao", p.getDescricao() != null ? p.getDescricao() : "");
                prod.put("preco", p.getPreco());
                prod.put("quantidade_estoque", estoque);
                prod.put("status", p.getStatus());
                prod.put("categoria_nome", "Produto");
                prod.put("categoria_id", p.getCategoriaId());
                prod.put("data_cadastro", p.getDataCadastro());
                
                produtosList.add(prod);
            }
            
            response.put("success", true);
            response.put("produtos", produtosList);
            response.put("total", produtosList.size());
            
            System.out.println("✅ Encontrados " + produtosList.size() + " produtos disponíveis para venda");
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao buscar produtos: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("error", "Erro ao buscar produtos: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Registrar uma nova venda - Versão que aceita Map
     * CORREÇÃO: Removido o try-catch para permitir que o Spring gerencie o rollback
     */
    @Transactional
    public Map<String, Object> registrarVenda(Map<String, Object> vendaData, Long usuarioId) {
        System.out.println("💰 registrarVenda - Registrando venda no banco de dados!");
        
        // Extrair dados do Map
        String formaPagamento = (String) vendaData.get("forma_pagamento");
        BigDecimal subtotal = (BigDecimal) vendaData.get("subtotal");
        BigDecimal desconto = (BigDecimal) vendaData.get("desconto");
        BigDecimal total = (BigDecimal) vendaData.get("total");
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> produtosRaw = (List<Map<String, Object>>) vendaData.get("produtos");
        
        // Validar dados
        if (produtosRaw == null || produtosRaw.isEmpty()) {
            throw new RuntimeException("Nenhum produto na venda");
        }
        
        if (total == null || total.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor total inválido");
        }
        
        System.out.println("📊 Dados da venda:");
        System.out.println("  Usuário ID: " + usuarioId);
        System.out.println("  Subtotal: " + subtotal);
        System.out.println("  Desconto: " + desconto);
        System.out.println("  Total: " + total);
        System.out.println("  Forma Pagamento: " + formaPagamento);
        System.out.println("  Quantidade de itens: " + produtosRaw.size());
        
        // 1. Criar a venda
        Venda venda = new Venda();
        venda.setUsuarioId(usuarioId);
        venda.setDataHora(LocalDateTime.now());
        venda.setValorTotal(total);
        venda.setFormaPagamento(formaPagamento);
        venda.setStatus("concluido");
        
        Venda savedVenda = vendaRepository.save(venda);
        System.out.println("✅ Venda criada com ID: " + savedVenda.getId());
        
        // 2. Salvar itens da venda e atualizar estoque
        BigDecimal somaSubtotais = BigDecimal.ZERO;
        
        for (Map<String, Object> itemRaw : produtosRaw) {
            // Extrair dados do produto
            Long produtoId = ((Number) itemRaw.get("id")).longValue();
            Integer quantidade = ((Number) itemRaw.get("quantidade")).intValue();
            BigDecimal precoUnitario = (BigDecimal) itemRaw.get("preco");
            
            // Validar quantidade
            if (quantidade <= 0) {
                throw new RuntimeException("Quantidade inválida para o produto ID: " + produtoId);
            }
            
            // Buscar produto no banco
            Optional<Produto> produtoOpt = produtoRepository.findById(produtoId);
            if (!produtoOpt.isPresent()) {
                throw new RuntimeException("Produto com ID " + produtoId + " não encontrado");
            }
            
            Produto produto = produtoOpt.get();
            
            // Validar estoque
            Integer estoqueAtual = produto.getQuantidadeEstoque() != null ? produto.getQuantidadeEstoque() : 0;
            if (estoqueAtual < quantidade) {
                throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome() + 
                    ". Disponível: " + estoqueAtual + ", Solicitado: " + quantidade);
            }
            
            // Calcular subtotal do item
            BigDecimal subtotalItem = precoUnitario.multiply(BigDecimal.valueOf(quantidade));
            somaSubtotais = somaSubtotais.add(subtotalItem);
            
            // Criar item da venda
            VendaItem item = new VendaItem();
            item.setVendaId(savedVenda.getId());
            item.setProdutoId(produtoId);
            item.setQuantidade(quantidade);
            item.setPrecoUnitario(precoUnitario);
            item.setSubtotal(subtotalItem);
            vendaItemRepository.save(item);
            
            // Atualizar estoque
            int novoEstoque = estoqueAtual - quantidade;
            produto.setQuantidadeEstoque(novoEstoque);
            produtoRepository.save(produto);
            
            System.out.println("  Item: " + produto.getNome() + 
                " | Qtd: " + quantidade + 
                " | Preço: R$ " + precoUnitario +
                " | Subtotal: R$ " + subtotalItem +
                " | Novo estoque: " + novoEstoque);
        }
        
        // Validar soma dos subtotais
        if (Math.abs(somaSubtotais.subtract(subtotal).doubleValue()) > 0.01) {
            System.out.println("⚠️ Aviso: Soma dos subtotais (R$ " + somaSubtotais + 
                ") difere do subtotal informado (R$ " + subtotal + ")");
        }
        
        // 3. Registrar no caixa
        Caixa caixa = new Caixa();
        caixa.setTipoMovimento("Venda");
        caixa.setValor(total);
        caixa.setDataHora(LocalDateTime.now());
        caixa.setUsuarioId(usuarioId);
        caixa.setFormaPagamento(formaPagamento);
        caixa.setVendaId(savedVenda.getId());
        caixaRepository.save(caixa);
        
        System.out.println("✅ Movimento de caixa registrado: + R$ " + total);
        System.out.println("🎉 Venda finalizada com sucesso!");
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Venda registrada com sucesso!");
        response.put("id_venda", savedVenda.getId());
        response.put("data_hora", LocalDateTime.now().toString());
        response.put("valor_total", total);
        
        return response;
    }
    
    /**
     * Cancelar uma venda (estornar estoque e caixa)
     * CORREÇÃO: Removido o try-catch para permitir que o Spring gerencie o rollback
     */
    @Transactional
    public Map<String, Object> cancelarVenda(Map<String, Object> data, Long usuarioId) {
        System.out.println("❌ cancelarVenda - Cancelando venda!");
        
        Long vendaId = ((Number) data.get("venda_id")).longValue();
        String motivo = (String) data.getOrDefault("motivo", "Cancelamento pelo usuário");
        
        System.out.println("Cancelando venda ID: " + vendaId);
        System.out.println("Motivo: " + motivo);
        System.out.println("Usuário: " + usuarioId);
        
        // Buscar venda
        Optional<Venda> vendaOpt = vendaRepository.findById(vendaId);
        if (!vendaOpt.isPresent()) {
            throw new RuntimeException("Venda não encontrada");
        }
        
        Venda venda = vendaOpt.get();
        
        if (!"concluido".equals(venda.getStatus())) {
            throw new RuntimeException("Venda já está " + venda.getStatus() + ", não pode ser cancelada");
        }
        
        // Buscar itens da venda
        List<VendaItem> itens = vendaItemRepository.findItensByVendaId(vendaId);
        
        // Estornar estoque
        for (VendaItem item : itens) {
            Optional<Produto> produtoOpt = produtoRepository.findById(item.getProdutoId());
            if (produtoOpt.isPresent()) {
                Produto produto = produtoOpt.get();
                Integer estoqueAtual = produto.getQuantidadeEstoque() != null ? produto.getQuantidadeEstoque() : 0;
                int novoEstoque = estoqueAtual + item.getQuantidade();
                produto.setQuantidadeEstoque(novoEstoque);
                produtoRepository.save(produto);
                System.out.println("Estoque restaurado para produto " + produto.getNome() + ": " + novoEstoque);
            }
        }
        
        // Registrar estorno no caixa
        Caixa caixa = new Caixa();
        caixa.setTipoMovimento("Estorno");
        caixa.setValor(venda.getValorTotal().negate());
        caixa.setDataHora(LocalDateTime.now());
        caixa.setUsuarioId(usuarioId);
        caixa.setFormaPagamento(venda.getFormaPagamento());
        caixa.setVendaId(vendaId);
        caixaRepository.save(caixa);
        
        // Atualizar status da venda
        venda.setStatus("cancelado");
        vendaRepository.save(venda);
        
        System.out.println("✅ Venda cancelada com sucesso!");
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Venda cancelada com sucesso!");
        response.put("id_venda", vendaId);
        response.put("valor_estornado", venda.getValorTotal());
        
        return response;
    }
    
    /**
     * Listar vendas com filtros
     */
    public Map<String, Object> listarVendas(LocalDate dataInicio, LocalDate dataFim, String formaPagamento) {
        System.out.println("📋 listarVendas - Buscando vendas do banco de dados!");
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Venda> vendas = vendaRepository.findVendasWithFilters(dataInicio, dataFim, formaPagamento);
            
            List<Map<String, Object>> vendasList = new ArrayList<>();
            for (Venda v : vendas) {
                Map<String, Object> vendaMap = new HashMap<>();
                vendaMap.put("id", v.getId());
                vendaMap.put("usuario_id", v.getUsuarioId());
                vendaMap.put("data_hora", v.getDataHora());
                vendaMap.put("valor_total", v.getValorTotal());
                vendaMap.put("forma_pagamento", v.getFormaPagamento());
                vendaMap.put("status", v.getStatus());
                
                // Buscar itens da venda
                List<VendaItem> itens = vendaItemRepository.findItensByVendaId(v.getId());
                List<Map<String, Object>> itensList = new ArrayList<>();
                for (VendaItem item : itens) {
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("produto_id", item.getProdutoId());
                    itemMap.put("quantidade", item.getQuantidade());
                    itemMap.put("preco_unitario", item.getPrecoUnitario());
                    itemMap.put("subtotal", item.getSubtotal());
                    
                    // Buscar nome do produto
                    produtoRepository.findById(item.getProdutoId()).ifPresent(produto -> {
                        itemMap.put("produto_nome", produto.getNome());
                    });
                    
                    itensList.add(itemMap);
                }
                vendaMap.put("itens", itensList);
                
                vendasList.add(vendaMap);
            }
            
            response.put("success", true);
            response.put("vendas", vendasList);
            response.put("total", vendasList.size());
            
            System.out.println("✅ Encontradas " + vendasList.size() + " vendas");
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao listar vendas: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("error", "Erro ao listar vendas: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Listar movimentos de caixa
     */
    public Map<String, Object> listarCaixa(LocalDate dataInicio, LocalDate dataFim) {
        System.out.println("💰 listarCaixa - Buscando movimentos de caixa!");
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (dataInicio == null) {
                dataInicio = LocalDate.now().minusDays(30);
            }
            if (dataFim == null) {
                dataFim = LocalDate.now();
            }
            
            List<Caixa> movimentos = caixaRepository.findMovimentosByPeriodo(dataInicio, dataFim);
            
            List<Map<String, Object>> movimentosList = new ArrayList<>();
            BigDecimal totalVendas = BigDecimal.ZERO;
            BigDecimal totalSangrias = BigDecimal.ZERO;
            BigDecimal totalSuprimentos = BigDecimal.ZERO;
            
            for (Caixa c : movimentos) {
                Map<String, Object> movMap = new HashMap<>();
                movMap.put("id", c.getId());
                movMap.put("tipo_movimento", c.getTipoMovimento());
                movMap.put("valor", c.getValor());
                movMap.put("data_hora", c.getDataHora());
                movMap.put("forma_pagamento", c.getFormaPagamento());
                movMap.put("usuario_id", c.getUsuarioId());
                movMap.put("venda_id", c.getVendaId());
                
                movimentosList.add(movMap);
                
                // Somar totais
                if ("Venda".equals(c.getTipoMovimento())) {
                    totalVendas = totalVendas.add(c.getValor());
                } else if ("Sangria".equals(c.getTipoMovimento())) {
                    totalSangrias = totalSangrias.add(c.getValor());
                } else if ("Suprimento".equals(c.getTipoMovimento())) {
                    totalSuprimentos = totalSuprimentos.add(c.getValor());
                }
            }
            
            BigDecimal saldoFinal = totalVendas.subtract(totalSangrias).add(totalSuprimentos);
            
            response.put("success", true);
            response.put("movimentos", movimentosList);
            response.put("resumo", Map.of(
                "total_vendas", totalVendas,
                "total_sangrias", totalSangrias,
                "total_suprimentos", totalSuprimentos,
                "saldo_final", saldoFinal
            ));
            response.put("periodo", Map.of(
                "data_inicio", dataInicio.toString(),
                "data_fim", dataFim.toString()
            ));
            
            System.out.println("✅ Encontrados " + movimentosList.size() + " movimentos de caixa");
            System.out.println("   Total vendas: R$ " + totalVendas);
            System.out.println("   Saldo final: R$ " + saldoFinal);
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao listar caixa: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("error", "Erro ao listar caixa: " + e.getMessage());
        }
        
        return response;
    }
}