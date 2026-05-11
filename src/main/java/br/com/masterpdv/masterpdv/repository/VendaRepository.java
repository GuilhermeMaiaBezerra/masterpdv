package br.com.masterpdv.masterpdv.repository;

import br.com.masterpdv.masterpdv.entity.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {
    
    // Métodos existentes do Dashboard
    @Query("SELECT COUNT(p) FROM Produto p WHERE p.status = 1")
    Long countProdutosAtivos();
    
    @Query("SELECT COUNT(v) as totalVendas, COALESCE(SUM(v.valorTotal), 0) as faturamento " +
           "FROM Venda v " +
           "WHERE FUNCTION('MONTH', v.dataHora) = FUNCTION('MONTH', CURRENT_DATE) " +
           "AND FUNCTION('YEAR', v.dataHora) = FUNCTION('YEAR', CURRENT_DATE) " +
           "AND v.status = 'concluido'")
    List<Object[]> getVendasFaturamentoMes();
    
    @Query("SELECT COALESCE(SUM(v.valorTotal), 0) " +
           "FROM Venda v " +
           "WHERE FUNCTION('MONTH', v.dataHora) = FUNCTION('MONTH', CURRENT_DATE - 1 MONTH) " +
           "AND FUNCTION('YEAR', v.dataHora) = FUNCTION('YEAR', CURRENT_DATE - 1 MONTH) " +
           "AND v.status = 'concluido'")
    BigDecimal getFaturamentoMesAnterior();
    
    @Query("SELECT v.formaPagamento, COUNT(v) as quantidade, SUM(v.valorTotal) as total " +
           "FROM Venda v " +
           "WHERE FUNCTION('MONTH', v.dataHora) = FUNCTION('MONTH', CURRENT_DATE) " +
           "AND FUNCTION('YEAR', v.dataHora) = FUNCTION('YEAR', CURRENT_DATE) " +
           "AND v.status = 'concluido' " +
           "GROUP BY v.formaPagamento " +
           "ORDER BY total DESC")
    List<Object[]> getVendasPorFormaPagamento();
    
    @Query("SELECT DATE(v.dataHora) as data, COUNT(v) as quantidade, SUM(v.valorTotal) as total " +
           "FROM Venda v " +
           "WHERE v.dataHora >= :dataInicio " +
           "AND v.status = 'concluido' " +
           "GROUP BY DATE(v.dataHora) " +
           "ORDER BY data ASC")
    List<Object[]> getVendasUltimos7Dias(@Param("dataInicio") LocalDateTime dataInicio);
    
    // ADICIONE ESTES MÉTODOS - usados no VendaService
    @Query("SELECT v FROM Venda v WHERE " +
           "(:dataInicio IS NULL OR DATE(v.dataHora) >= :dataInicio) AND " +
           "(:dataFim IS NULL OR DATE(v.dataHora) <= :dataFim) AND " +
           "(:formaPagamento IS NULL OR v.formaPagamento = :formaPagamento) " +
           "ORDER BY v.dataHora DESC")
    List<Venda> findVendasWithFilters(@Param("dataInicio") LocalDate dataInicio,
                                       @Param("dataFim") LocalDate dataFim,
                                       @Param("formaPagamento") String formaPagamento);
    
    Optional<Venda> findByIdAndStatus(Long id, String status);
}