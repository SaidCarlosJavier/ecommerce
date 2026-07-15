package supermercado.pagos.service;

import org.springframework.stereotype.Service;
import supermercado.pagos.discount.BasePriceCalculator;
import supermercado.pagos.discount.PriceCalculator;
import supermercado.pagos.discount.TaxDecorator;
import supermercado.pagos.model.Transaction;
import supermercado.pagos.observer.PaymentEventManager;
import supermercado.pagos.observer.ReceiptGenerator;
import supermercado.pagos.payment.PaymentProcessor;
import supermercado.pagos.payment.PaymentProcessorFactory;
import supermercado.pagos.repository.ProductRepository;
import supermercado.pagos.repository.TransactionDetailRepository;
import supermercado.pagos.repository.TransactionRepository;
import supermercado.pagos.state.FailedState;
import supermercado.pagos.state.PaidState;
import supermercado.pagos.state.TransactionContext;

import java.math.BigDecimal;

@Service
public class PaymentService {

    private final PaymentProcessorFactory factory;
    private final ProductRepository productRepository;
    private final TransactionDetailRepository detailRepository;
    private final TransactionRepository transactionRepository;

    public PaymentService(PaymentProcessorFactory factory,
                           ProductRepository productRepository,
                           TransactionDetailRepository detailRepository,
                           TransactionRepository transactionRepository) {
        this.factory = factory;
        this.productRepository = productRepository;
        this.detailRepository = detailRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Antes este método era void. Ahora devuelve la Transaction ya actualizada
     * (con su estado final PAGADO/FALLIDO) para que el controller pueda
     * responder al frontend con el resultado real del pago.
     */
    public Transaction processTransaction(Transaction transaction, String methodType) {

        // Decorator: calcula el monto final aplicando impuestos sobre el subtotal
        PriceCalculator calculator = new TaxDecorator(new BasePriceCalculator());
        double subtotal = transaction.getMontoSubtotal().doubleValue();
        double finalAmount = calculator.calculate(subtotal);

        // FIX: antes el monto final del decorator se calculaba pero nunca
        // se guardaba en la transacción (impuestos/montoTotal quedaban en 0).
        transaction.setImpuestos(BigDecimal.valueOf(finalAmount - subtotal));
        transaction.setMontoTotal(BigDecimal.valueOf(finalAmount));

        // Factory: obtiene el procesador de pago correcto (Card/Cash/QR)
        PaymentProcessor processor = factory.getPaymentProcessor(methodType);
        boolean success = processor.processPayment(finalAmount);

        transaction.setMetodoPago(methodType.toUpperCase());

        // State: mueve la transacción a PAGADO o FALLIDO y persiste
        TransactionContext context = new TransactionContext();

        // Observer: notifica (ej. genera el "recibo") solo si el pago fue exitoso
        PaymentEventManager eventManager = new PaymentEventManager();
        eventManager.subscribe(new ReceiptGenerator());

        if (success) {
            context.setState(new PaidState(productRepository, detailRepository, transactionRepository));
            context.process(transaction);
            eventManager.notifyObservers(transaction);
        } else {
            context.setState(new FailedState(transactionRepository));
            context.process(transaction);
        }

        return transaction;
    }
}
