package br.com.masterpdv.masterpdv.controller;

import br.com.masterpdv.masterpdv.service.VendaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/vendas")
public class VendaController {
    
    @Autowired
    private VendaService vendaService;
    
    public VendaController() {
        System.out.println(">>> CONSTRUTOR: VendaController foi INSTANCIADO! <<<");
    }
    
    @GetMapping("/listar_produtos")
    public Map<String, Object> listarProdutos(
            @RequestParam(required = false) String busca,
            HttpSession session) {
        
        System.out.println("🚀 ENDPOINT CHAMADO: /api/vendas/listar_produtos");
        System.out.println("   Busca: " + busca);
        System.out.println("   Session ID: " + session.getId());
        
        Object usuarioIdObj = session.getAttribute("usuario_id");
        System.out.println("   Usuario ID na sessão: " + usuarioIdObj);
        
        if (usuarioIdObj == null) {
            System.out.println("❌ Usuário NÃO autorizado!");
            return Map.of("success", false, "error", "Não autorizado");
        }
        
        System.out.println("✅ Usuário autorizado. Chamando service...");
        return vendaService.listarProdutosPDV(busca);
    }
    
    @SuppressWarnings("unchecked")
    @PostMapping("/registrar_venda")
    public Map<String, Object> registrarVenda(@RequestBody Map<String, Object> request, HttpSession session) {
        System.out.println("=== REGISTRAR VENDA ===");
        
        // CORREÇÃO: Usar Number para converter de forma segura
        Object usuarioIdObj = session.getAttribute("usuario_id");
        if (usuarioIdObj == null) {
            return Map.of("success", false, "error", "Não autorizado");
        }
        Long usuarioId = ((Number) usuarioIdObj).longValue();
        
        try {
            // Extrair dados básicos
            String formaPagamento = (String) request.get("forma_pagamento");
            
            // Extrair e converter números
            BigDecimal subtotal = BigDecimal.valueOf(((Number) request.get("subtotal")).doubleValue());
            BigDecimal desconto = BigDecimal.valueOf(((Number) request.get("desconto")).doubleValue());
            BigDecimal total = BigDecimal.valueOf(((Number) request.get("total")).doubleValue());
            
            // Extrair lista de produtos
            List<Map<String, Object>> produtosRaw = (List<Map<String, Object>>) request.get("produtos");
            List<Map<String, Object>> produtosConvertidos = new ArrayList<>();
            
            for (Map<String, Object> prodRaw : produtosRaw) {
                Map<String, Object> prodConvertido = new HashMap<>();
                
                // Converter ID para Long
                Number idNumber = (Number) prodRaw.get("id");
                prodConvertido.put("id", idNumber.longValue());
                
                prodConvertido.put("nome", prodRaw.get("nome"));
                prodConvertido.put("quantidade", ((Number) prodRaw.get("quantidade")).intValue());
                prodConvertido.put("preco", BigDecimal.valueOf(((Number) prodRaw.get("preco")).doubleValue()));
                
                produtosConvertidos.add(prodConvertido);
            }
            
            // Criar map com os dados convertidos
            Map<String, Object> vendaData = new HashMap<>();
            vendaData.put("produtos", produtosConvertidos);
            vendaData.put("forma_pagamento", formaPagamento);
            vendaData.put("subtotal", subtotal);
            vendaData.put("desconto", desconto);
            vendaData.put("total", total);
            
            System.out.println("Dados convertidos com sucesso!");
            System.out.println("  Produtos: " + produtosConvertidos.size());
            System.out.println("  Total: " + total);
            
            return vendaService.registrarVenda(vendaData, usuarioId);
            
        } catch (Exception e) {
            System.err.println("Erro ao processar venda: " + e.getMessage());
            e.printStackTrace();
            return Map.of("success", false, "error", "Erro ao processar venda: " + e.getMessage());
        }
    }
    
    @PostMapping("/cancelar_venda")
    public Map<String, Object> cancelarVenda(@RequestBody Map<String, Object> data, HttpSession session) {
        // CORREÇÃO: Usar Number para converter de forma segura
        Object usuarioIdObj = session.getAttribute("usuario_id");
        if (usuarioIdObj == null) {
            return Map.of("success", false, "error", "Não autorizado");
        }
        Long usuarioId = ((Number) usuarioIdObj).longValue();
        
        return vendaService.cancelarVenda(data, usuarioId);
    }
    
    @GetMapping("/listar_vendas")
    public Map<String, Object> listarVendas(HttpSession session) {
        if (session.getAttribute("usuario_id") == null) {
            return Map.of("success", false, "error", "Não autorizado");
        }
        return vendaService.listarVendas(null, null, null);
    }
    
    @GetMapping("/listar_caixa")
    public Map<String, Object> listarCaixa(HttpSession session) {
        if (session.getAttribute("usuario_id") == null) {
            return Map.of("success", false, "error", "Não autorizado");
        }
        return vendaService.listarCaixa(null, null);
    }
}