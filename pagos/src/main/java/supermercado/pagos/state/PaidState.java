package supermercado.pagos.state;

import supermercado.pagos.model.Product;
import supermercado.pagos.model.Transaction;
import supermercado.pagos.model.TransactionDetail;
import supermercado.pagos.repository.ProductRepository;
import supermercado.pagos.repository.TransactionDetailRepository;

import java.util.List;

public class PaidState implements TransactionState {

    private final ProductRepository productRepository;
    private final TransactionDetailRepository detailRepository;

    public PaidState(ProductRepository productRepository,
                     TransactionDetailRepository detailRepository) {
        this.productRepository = productRepository;
        this.detailRepository = detailRepository;
    }

    @Override
    public void handle(TransactionContext context, Transaction transaction) {

        List<TransactionDetail> details = detailRepository.findByTransactionId(transaction.getId());

        for (TransactionDetail detail : details) {
            Product product = productRepository.findById(detail.getProduct().getId()).orElseThrow();

            product.setStock(product.getStock() - detail.getCantidad());
            productRepository.save(product);
        }

        System.out.println("Transacción PAGADA y stock actualizado");
    }
}
