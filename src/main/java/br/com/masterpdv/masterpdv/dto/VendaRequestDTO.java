package br.com.masterpdv.masterpdv.dto;

import java.math.BigDecimal;
import java.util.List;

public class VendaRequestDTO {
    private List<ProdutoVendaDTO> produtos;
    private String forma_pagamento;
    private BigDecimal subtotal;
    private BigDecimal desconto;
    private BigDecimal total;
    
    // Construtor padrão
    public VendaRequestDTO() {
    }
    
    // Getters e Setters
    public List<ProdutoVendaDTO> getProdutos() { return produtos; }
    public void setProdutos(List<ProdutoVendaDTO> produtos) { this.produtos = produtos; }
    
    public String getForma_pagamento() { return forma_pagamento; }
    public void setForma_pagamento(String forma_pagamento) { this.forma_pagamento = forma_pagamento; }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    
    public BigDecimal getDesconto() { return desconto; }
    public void setDesconto(BigDecimal desconto) { this.desconto = desconto; }
    
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    
    public static class ProdutoVendaDTO {
        private Object id;  // ← Mudamos para Object para aceitar Integer e Long
        private String nome;
        private BigDecimal preco;
        private Integer quantidade;
        
        // Construtor padrão
        public ProdutoVendaDTO() {
        }
        
        // Getter que retorna Long (convertendo se necessário)
        public Long getId() { 
            if (id instanceof Long) {
                return (Long) id;
            } else if (id instanceof Integer) {
                return ((Integer) id).longValue();
            } else if (id instanceof Number) {
                return ((Number) id).longValue();
            }
            return null;
        }
        
        // Setter que aceita qualquer tipo de número
        public void setId(Object id) { 
            this.id = id;
        }
        
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        
        public BigDecimal getPreco() { return preco; }
        public void setPreco(BigDecimal preco) { this.preco = preco; }
        
        public Integer getQuantidade() { return quantidade; }
        public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    }
}