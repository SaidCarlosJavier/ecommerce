package supermercado.pagos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import supermercado.pagos.model.TransactionDetail;

import java.util.List;

public interface TransactionDetailRepository extends JpaRepository<TransactionDetail, Long> {

    List<TransactionDetail> findByProductId(Long productId);

    List<TransactionDetail> findByTransactionId(Long id);
}
