package br.com.masterpdv.masterpdv.controller;

import br.com.masterpdv.masterpdv.service.LoginService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    @GetMapping("/")
    public String index(HttpSession session) {
        if (session.getAttribute("usuario_id") != null) {
            return "redirect:/home";
        }
        return "index";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String usuario,
            @RequestParam String senha,
            HttpSession session,
            Model model
    ) {
        System.out.println("=== RECEBENDO REQUISIÇÃO DE LOGIN ===");
        System.out.println("Usuário: " + usuario);
        
        Integer id = loginService.validarLogin(usuario, senha);
        System.out.println("ID retornado do service: " + id);

        if (id != null) {
            session.setAttribute("usuario_id", id);
            session.setAttribute("usuario", usuario);
            System.out.println("Login bem sucedido! Redirecionando para home");
            return "redirect:/home";
        }

        System.out.println("Login falhou! Adicionando mensagem de erro");
        model.addAttribute("erro", "Usuário ou senha inválidos");
        return "index";
    }
    
    @GetMapping("/home")
    public String home(HttpSession session) {
        if (session.getAttribute("usuario_id") == null) {
            return "redirect:/";
        }
        return "home";
    }

        @GetMapping("/vendas")
    public String vendas(HttpSession session) {
        if (session.getAttribute("usuario_id") == null) {
            return "redirect:/";
        }
        return "vendas";
    }
    
   
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Invalida a sessão atual
        session.invalidate();
        // Redireciona para a página de login
        return "redirect:/";
    }
    
    @GetMapping("/produtos")
public String produtos(HttpSession session) {
    if (session.getAttribute("usuario_id") == null) {
        return "redirect:/";
    }
    return "produtos"; // Vai buscar templates/produtos.html
}


}