package supermercado.pagos.state;

import supermercado.pagos.model.Product;
import supermercado.pagos.model.Transaction;
import supermercado.pagos.model.TransactionDetail;
import supermercado.pagos.repository.ProductRepository;
import supermercado.pagos.repository.TransactionDetailRepository;
import supermercado.pagos.repository.TransactionRepository;

import java.util.List;

public class PaidState implements TransactionState {

    private final ProductRepository productRepository;
    private final TransactionDetailRepository detailRepository;
    private final TransactionRepository transactionRepository;

    public PaidState(ProductRepository productRepository,
                      TransactionDetailRepository detailRepository,
                      TransactionRepository transactionRepository) {
        this.productRepository = productRepository;
        this.detailRepository = detailRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void handle(TransactionContext context, Transaction transaction) {

        List<TransactionDetail> details = detailRepository.findByTransactionId(transaction.getId());

        // Solo aquí, cuando el pago fue confirmado, se descuenta el stock real
        for (TransactionDetail detail : details) {
            Product product = productRepository.findById(detail.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (product.getStock() < detail.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para " + product.getNombre());
            }

            product.setStock(product.getStock() - detail.getCantidad());
            productRepository.save(product);
        }

        // FIX: antes esto no se guardaba y la transacción quedaba "PENDIENTE" para siempre
        transaction.setEstado("PAGADO");
        transactionRepository.save(transaction);

        System.out.println("Transacción PAGADA y stock actualizado");
    }
}
