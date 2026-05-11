package br.com.masterpdv.masterpdv.repository;

import br.com.masterpdv.masterpdv.entity.VendaItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VendaItemRepository extends JpaRepository<VendaItem, Long> {
    
    // ===== MÉTODO EXISTENTE DO DASHBOARD =====
    
    @Query(value = "SELECT p.nome, SUM(vi.quantidade) as total_vendido, SUM(vi.subtotal) as total_faturado " +
           "FROM tb_venda_item vi " +
           "INNER JOIN tb_produto p ON vi.id_produto = p.id_produto " +
           "INNER JOIN tb_venda v ON vi.id_venda = v.ID_venda " +
           "WHERE v.status = 'concluido' " +
           "AND MONTH(v.data_hora) = MONTH(CURRENT_DATE()) " +
           "AND YEAR(v.data_hora) = YEAR(CURRENT_DATE()) " +
           "GROUP BY vi.id_produto, p.nome " +
           "ORDER BY total_vendido DESC " +
           "LIMIT 5", nativeQuery = true)
    List<Object[]> findTopProdutosMes();
    
    // ===== NOVOS MÉTODOS PARA VENDAS/PDV =====
    
    List<VendaItem> findByVendaId(Long vendaId);
    
    @Query("SELECT vi FROM VendaItem vi WHERE vi.vendaId = :vendaId")
    List<VendaItem> findItensByVendaId(Long vendaId);
}