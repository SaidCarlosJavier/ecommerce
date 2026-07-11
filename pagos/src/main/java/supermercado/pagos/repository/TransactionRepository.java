package supermercado.pagos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import supermercado.pagos.model.Transaction;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserId(Long userId);
}