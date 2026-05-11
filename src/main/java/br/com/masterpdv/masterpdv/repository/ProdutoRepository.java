package br.com.masterpdv.masterpdv.repository;

import br.com.masterpdv.masterpdv.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    
    @Query("SELECT p FROM Produto p WHERE " +
           "(:categoriaId IS NULL OR p.categoriaId = :categoriaId) AND " +
           "(:nome IS NULL OR p.nome LIKE %:nome%) " +
           "ORDER BY p.id DESC")
    List<Produto> findWithFilters(@Param("categoriaId") Long categoriaId, 
                                   @Param("nome") String nome);
    
    // ADICIONE ESTE MÉTODO - é ESSENCIAL para o VendaService
    @Query("SELECT p FROM Produto p WHERE p.status = 1 AND " +
           "(:busca IS NULL OR :busca = '' OR p.nome LIKE %:busca% OR p.descricao LIKE %:busca%) " +
           "ORDER BY p.nome ASC")
    List<Produto> findProdutosAtivosWithBusca(@Param("busca") String busca);
}