package br.com.masterpdv.masterpdv.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_venda")
public class Venda {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_venda")
    private Long id;
    
    @Column(name = "id_usuario")
    private Long usuarioId;
    
    @Column(name = "data_hora")
    private LocalDateTime dataHora;
    
    @Column(name = "valor_total")
    private BigDecimal valorTotal;
    
    @Column(name = "forma_pagamento")
    private String formaPagamento;
    
    private String status;
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    
    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}