package br.com.masterpdv.masterpdv.service;

import br.com.masterpdv.masterpdv.entity.Produto;
import br.com.masterpdv.masterpdv.entity.Categoria;
import br.com.masterpdv.masterpdv.repository.ProdutoRepository;
import br.com.masterpdv.masterpdv.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ProdutoService {
    
    @Autowired
    private ProdutoRepository produtoRepository;
    
    @Autowired
    private CategoriaRepository categoriaRepository;
    
    public Map<String, Object> listarProdutos(Long categoriaId, String nome) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Produto> produtos = produtoRepository.findWithFilters(categoriaId, nome);
            
            List<Map<String, Object>> produtosList = new ArrayList<>();
            for (Produto p : produtos) {
                Map<String, Object> prod = new HashMap<>();
                prod.put("id_produto", p.getId());
                prod.put("nome", p.getNome());
                prod.put("descricao", p.getDescricao());
                prod.put("preco", p.getPreco());
                prod.put("quantidade_estoque", p.getQuantidadeEstoque());
                prod.put("categoria_id", p.getCategoriaId());
                prod.put("data_cadastro", p.getDataCadastro());
                prod.put("status", p.getStatus());
                produtosList.add(prod);
            }
            
            response.put("success", true);
            response.put("produtos", produtosList);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Erro ao listar produtos: " + e.getMessage());
        }
        
        return response;
    }
    
    public Map<String, Object> listarCategorias() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Categoria> categorias = categoriaRepository.findAllByOrderByNomeAsc();
            
            List<Map<String, Object>> categoriasList = new ArrayList<>();
            for (Categoria c : categorias) {
                Map<String, Object> cat = new HashMap<>();
                cat.put("id_categoria", c.getId());
                cat.put("nome", c.getNome());
                categoriasList.add(cat);
            }
            
            response.put("success", true);
            response.put("categorias", categoriasList);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Erro ao listar categorias: " + e.getMessage());
        }
        
        return response;
    }
    
public Map<String, Object> cadastrarProduto(Map<String, Object> data) {
    Map<String, Object> response = new HashMap<>();
    
    try {
        String nome = (String) data.get("nome");
        String descricao = data.get("descricao") != null ? (String) data.get("descricao") : "";
        
        BigDecimal preco = BigDecimal.ZERO;
        Object precoObj = data.get("preco");
        if (precoObj != null) {
            if (precoObj instanceof Number) {
                preco = BigDecimal.valueOf(((Number) precoObj).doubleValue());
            } else if (precoObj instanceof String) {
                preco = new BigDecimal((String) precoObj);
            }
        }
        
        Integer quantidadeEstoque = 0;
        Object qtdObj = data.get("quantidade_estoque");
        if (qtdObj != null) {
            if (qtdObj instanceof Number) {
                quantidadeEstoque = ((Number) qtdObj).intValue();
            } else if (qtdObj instanceof String) {
                quantidadeEstoque = Integer.parseInt((String) qtdObj);
            }
        }
        
        Long categoriaId = null;
        Object catObj = data.get("categoria_id");
        if (catObj != null && !catObj.toString().isEmpty()) {
            if (catObj instanceof Number) {
                categoriaId = ((Number) catObj).longValue();
            } else if (catObj instanceof String) {
                categoriaId = Long.parseLong((String) catObj);
            }
        }
        
        Integer status = 1;
        Object statusObj = data.get("status");
        if (statusObj != null) {
            if (statusObj instanceof Number) {
                status = ((Number) statusObj).intValue();
            } else if (statusObj instanceof String) {
                status = Integer.parseInt((String) statusObj);
            }
        }
        
        // Validação
        if (nome == null || nome.isEmpty()) {
            response.put("success", false);
            response.put("message", "Nome é obrigatório");
            return response;
        }
        
        if (preco.compareTo(BigDecimal.ZERO) <= 0) {
            response.put("success", false);
            response.put("message", "Preço deve ser maior que zero");
            return response;
        }
        
        // Criar produto
        Produto produto = new Produto();
        produto.setNome(nome);
        produto.setDescricao(descricao);
        produto.setPreco(preco);
        produto.setQuantidadeEstoque(quantidadeEstoque);
        produto.setCategoriaId(categoriaId);
        produto.setStatus(status);
        produto.setDataCadastro(LocalDateTime.now());
        
        Produto saved = produtoRepository.save(produto);
        
        response.put("success", true);
        response.put("message", "Produto cadastrado com sucesso");
        response.put("id_produto", saved.getId());
        
    } catch (Exception e) {
        e.printStackTrace();
        response.put("success", false);
        response.put("message", "Erro ao cadastrar: " + e.getMessage());
    }
    
    return response;
}
    
public Map<String, Object> editarProduto(Map<String, Object> data) {
    Map<String, Object> response = new HashMap<>();
    
    try {
        System.out.println("=== EDITANDO PRODUTO ===");
        System.out.println("Dados recebidos: " + data);
        
        // Tratamento do ID (pode vir como Integer, Long ou String)
        Long id = null;
        Object idObj = data.get("id_produto");
        if (idObj != null) {
            if (idObj instanceof Number) {
                id = ((Number) idObj).longValue();
            } else if (idObj instanceof String) {
                id = Long.parseLong((String) idObj);
            }
        }
        
        // Nome (sempre String)
        String nome = data.get("nome") != null ? (String) data.get("nome") : "";
        
        // Descrição (pode ser null)
        String descricao = data.get("descricao") != null ? (String) data.get("descricao") : "";
        
        // Preço (pode vir como String ou Number)
        BigDecimal preco = BigDecimal.ZERO;
        Object precoObj = data.get("preco");
        if (precoObj != null) {
            if (precoObj instanceof Number) {
                preco = BigDecimal.valueOf(((Number) precoObj).doubleValue());
            } else if (precoObj instanceof String) {
                String precoStr = (String) precoObj;
                if (!precoStr.isEmpty()) {
                    preco = new BigDecimal(precoStr);
                }
            }
        }
        
        // Quantidade em estoque
        Integer quantidadeEstoque = 0;
        Object qtdObj = data.get("quantidade_estoque");
        if (qtdObj != null) {
            if (qtdObj instanceof Number) {
                quantidadeEstoque = ((Number) qtdObj).intValue();
            } else if (qtdObj instanceof String) {
                String qtdStr = (String) qtdObj;
                if (!qtdStr.isEmpty()) {
                    quantidadeEstoque = Integer.parseInt(qtdStr);
                }
            }
        }
        
        // Categoria ID (pode ser null, string vazia, ou número)
        Long categoriaId = null;
        Object catObj = data.get("categoria_id");
        if (catObj != null && !catObj.toString().isEmpty()) {
            if (catObj instanceof Number) {
                categoriaId = ((Number) catObj).longValue();
            } else if (catObj instanceof String) {
                String catStr = (String) catObj;
                if (!catStr.isEmpty()) {
                    categoriaId = Long.parseLong(catStr);
                }
            }
        }
        
        // Status (padrão 1)
        Integer status = 1;
        Object statusObj = data.get("status");
        if (statusObj != null) {
            if (statusObj instanceof Number) {
                status = ((Number) statusObj).intValue();
            } else if (statusObj instanceof String) {
                String statusStr = (String) statusObj;
                if (!statusStr.isEmpty()) {
                    status = Integer.parseInt(statusStr);
                }
            }
        }
        
        // Validações
        if (id == null) {
            response.put("success", false);
            response.put("message", "ID do produto não informado");
            return response;
        }
        
        if (nome.isEmpty()) {
            response.put("success", false);
            response.put("message", "Nome é obrigatório");
            return response;
        }
        
        if (preco.compareTo(BigDecimal.ZERO) <= 0) {
            response.put("success", false);
            response.put("message", "Preço deve ser maior que zero");
            return response;
        }
        
        // Buscar produto existente
        Optional<Produto> optional = produtoRepository.findById(id);
        if (!optional.isPresent()) {
            response.put("success", false);
            response.put("message", "Produto não encontrado");
            return response;
        }
        
        // Atualizar produto
        Produto produto = optional.get();
        produto.setNome(nome);
        produto.setDescricao(descricao);
        produto.setPreco(preco);
        produto.setQuantidadeEstoque(quantidadeEstoque);
        produto.setCategoriaId(categoriaId);
        produto.setStatus(status);
    
        
        produtoRepository.save(produto);
        
        response.put("success", true);
        response.put("message", "Produto atualizado com sucesso");
        
    } catch (NumberFormatException e) {
        System.err.println("Erro de formato numérico: " + e.getMessage());
        response.put("success", false);
        response.put("message", "Erro no formato dos dados numéricos: " + e.getMessage());
    } catch (Exception e) {
        e.printStackTrace();
        response.put("success", false);
        response.put("message", "Erro ao atualizar: " + e.getMessage());
    }
    
    return response;
}
    
public Map<String, Object> excluirProduto(Map<String, Object> data) {
    Map<String, Object> response = new HashMap<>();
    
    try {
        Long id = null;
        Object idObj = data.get("id_produto");
        if (idObj != null) {
            if (idObj instanceof Number) {
                id = ((Number) idObj).longValue();
            } else if (idObj instanceof String) {
                id = Long.parseLong((String) idObj);
            }
        }
        
        if (id == null) {
            response.put("success", false);
            response.put("message", "ID do produto não informado");
            return response;
        }
        
        if (!produtoRepository.existsById(id)) {
            response.put("success", false);
            response.put("message", "Produto não encontrado");
            return response;
        }
        
        produtoRepository.deleteById(id);
        
        response.put("success", true);
        response.put("message", "Produto excluído com sucesso");
        
    } catch (Exception e) {
        e.printStackTrace();
        response.put("success", false);
        response.put("message", "Erro ao excluir: " + e.getMessage());
    }
    
    return response;
}
}