package br.com.masterpdv.masterpdv.repository;

import br.com.masterpdv.masterpdv.entity.Caixa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface CaixaRepository extends JpaRepository<Caixa, Long> {
    
    @Query("SELECT c FROM Caixa c WHERE DATE(c.dataHora) BETWEEN :dataInicio AND :dataFim ORDER BY c.dataHora DESC")
    List<Caixa> findMovimentosByPeriodo(@Param("dataInicio") LocalDate dataInicio,
                                         @Param("dataFim") LocalDate dataFim);
    
    @Query("SELECT " +
           "SUM(CASE WHEN c.tipoMovimento = 'Venda' THEN c.valor ELSE 0 END) as totalVendas, " +
           "SUM(CASE WHEN c.tipoMovimento = 'Sangria' THEN c.valor ELSE 0 END) as totalSangrias, " +
           "SUM(CASE WHEN c.tipoMovimento = 'Suprimento' THEN c.valor ELSE 0 END) as totalSuprimentos " +
           "FROM Caixa c WHERE DATE(c.dataHora) BETWEEN :dataInicio AND :dataFim")
    Object[] getResumoCaixa(@Param("dataInicio") LocalDate dataInicio,
                             @Param("dataFim") LocalDate dataFim);
}