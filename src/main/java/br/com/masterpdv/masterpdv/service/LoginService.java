package br.com.masterpdv.masterpdv.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Integer validarLogin(String usuario, String senha) {

        try {
            String sql = "SELECT id FROM tb_usuario WHERE usuario = ? AND senha = ?";
            System.out.println("SQL: " + sql);
            
            Integer id = jdbcTemplate.queryForObject(sql, Integer.class, usuario, senha);
            return id;
            
        } catch (Exception e) {
            System.out.println("ERRO: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}