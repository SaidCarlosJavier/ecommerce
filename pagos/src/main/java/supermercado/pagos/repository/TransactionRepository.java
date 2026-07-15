package supermercado.pagos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import supermercado.pagos.model.Transaction;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // Para la Fase 5: Historial del Perfil del Usuario
    List<Transaction> findByUserIdOrderByFechaDesc(Long userId);

    List<Transaction> findByUserId(Long cashierId);
}