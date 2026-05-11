package br.com.masterpdv.masterpdv.repository;

import br.com.masterpdv.masterpdv.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    
    @Query("SELECT c FROM Categoria c ORDER BY c.nome")
    List<Categoria> findAllOrderByNome();
    
    List<Categoria> findAllByOrderByNomeAsc();
   
}