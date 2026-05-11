package br.com.masterpdv.masterpdv.controller;

import br.com.masterpdv.masterpdv.service.DashboardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

@RestController
@RequestMapping("/api")
public class DashboardController {
    
    @Autowired
    private DashboardService dashboardService;
    
    @GetMapping("/dashboard-data")
    public Map<String, Object> getDashboardData(HttpSession session) {
        if (session.getAttribute("usuario_id") == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Não autorizado");
            return error;
        }
        
        return dashboardService.getDadosDashboard();
    }
    
    // ADICIONE ESTE MÉTODO MOCK PARA TESTE
    @GetMapping("/dashboard-data-simple")
    public Map<String, Object> getDashboardDataSimple() {
        System.out.println("=== ENDPOINT MOCK CHAMADO ===");
        
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> dashboard = new HashMap<>();
        
        // Dados mockados para teste
        dashboard.put("faturamento", "12.500,00");
        dashboard.put("vendas", 45);
        dashboard.put("produtos", 120);
        dashboard.put("crescimento", 15.5);
        
        List<Map<String, Object>> pagamentos = new ArrayList<>();
        Map<String, Object> dinheiro = new HashMap<>();
        dinheiro.put("forma", "Dinheiro");
        dinheiro.put("quantidade", 20);
        dinheiro.put("total", 5000.00);
        dinheiro.put("cor", "#FF6384");
        pagamentos.add(dinheiro);
        
        Map<String, Object> cartao = new HashMap<>();
        cartao.put("forma", "Cartão");
        cartao.put("quantidade", 25);
        cartao.put("total", 7500.00);
        cartao.put("cor", "#36A2EB");
        pagamentos.add(cartao);
        
        Map<String, Object> pix = new HashMap<>();
        pix.put("forma", "PIX");
        pix.put("quantidade", 15);
        pix.put("total", 4500.00);
        pix.put("cor", "#FFCE56");
        pagamentos.add(pix);
        
        List<Map<String, Object>> vendasSemana = new ArrayList<>();
        String[] dias = {"01/04", "02/04", "03/04", "04/04", "05/04", "06/04", "07/04"};
        Double[] valores = {1200.00, 980.00, 1500.00, 2100.00, 1800.00, 2500.00, 3000.00};
        int[] quantidades = {5, 4, 6, 8, 7, 10, 12};
        
        for (int i = 0; i < dias.length; i++) {
            Map<String, Object> dia = new HashMap<>();
            dia.put("data", dias[i]);
            dia.put("quantidade", quantidades[i]);
            dia.put("total", valores[i]);
            vendasSemana.add(dia);
        }
        
        List<Map<String, Object>> topProdutos = new ArrayList<>();
        String[] produtos = {"Produto A", "Produto B", "Produto C", "Produto D", "Produto E"};
        int[] qtds = {50, 35, 28, 20, 15};
        double[] totais = {2500.00, 1750.00, 1400.00, 1000.00, 750.00};
        
        for (int i = 0; i < produtos.length; i++) {
            Map<String, Object> produto = new HashMap<>();
            produto.put("nome", produtos[i]);
            produto.put("quantidade", qtds[i]);
            produto.put("faturamento", totais[i]);
            topProdutos.add(produto);
        }
        
        response.put("success", true);
        response.put("dashboard", dashboard);
        response.put("grafico_pagamentos", pagamentos);
        response.put("vendas_semana", vendasSemana);
        response.put("top_produtos", topProdutos);
        
        return response;
    }
}