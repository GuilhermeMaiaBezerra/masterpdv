package br.com.masterpdv.masterpdv.controller;

import br.com.masterpdv.masterpdv.service.ProdutoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {
    
    @Autowired
    private ProdutoService produtoService;
    
    @GetMapping("/listar")
    public Map<String, Object> listarProdutos(
            @RequestParam(required = false) Long categoria_id,
            @RequestParam(required = false) String nome,
            HttpSession session) {
        
        System.out.println("=== LISTAR PRODUTOS CHAMADO ===");
        System.out.println("categoria_id: " + categoria_id);
        System.out.println("nome: " + nome);
        
        if (session.getAttribute("usuario_id") == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Não autorizado");
            return error;
        }
        
        return produtoService.listarProdutos(categoria_id, nome);
    }
    
    @GetMapping("/categorias")
    public Map<String, Object> listarCategorias(HttpSession session) {
        System.out.println("=== LISTAR CATEGORIAS CHAMADO ===");
        
        if (session.getAttribute("usuario_id") == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Não autorizado");
            return error;
        }
        
        return produtoService.listarCategorias();
    }
    
    @PostMapping("/cadastrar")
    public Map<String, Object> cadastrarProduto(@RequestBody Map<String, Object> data, HttpSession session) {
        System.out.println("=== CADASTRAR PRODUTO CHAMADO ===");
        
        if (session.getAttribute("usuario_id") == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Não autorizado");
            return error;
        }
        
        return produtoService.cadastrarProduto(data);
    }
    
    @PostMapping("/editar")
    public Map<String, Object> editarProduto(@RequestBody Map<String, Object> data, HttpSession session) {
        System.out.println("=== EDITAR PRODUTO CHAMADO ===");
        
        if (session.getAttribute("usuario_id") == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Não autorizado");
            return error;
        }
        
        return produtoService.editarProduto(data);
    }
    
    @PostMapping("/excluir")
    public Map<String, Object> excluirProduto(@RequestBody Map<String, Object> data, HttpSession session) {
        System.out.println("=== EXCLUIR PRODUTO CHAMADO ===");
        
        if (session.getAttribute("usuario_id") == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Não autorizado");
            return error;
        }
        
        return produtoService.excluirProduto(data);
    }
}