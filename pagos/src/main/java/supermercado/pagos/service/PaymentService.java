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
import supermercado.pagos.state.FailedState;
import supermercado.pagos.state.PaidState;
import supermercado.pagos.state.TransactionContext;

@Service
public class PaymentService {

    private final PaymentProcessorFactory factory;
    private final ProductRepository productRepository;
    private final TransactionDetailRepository detailRepository;

    public PaymentService(PaymentProcessorFactory factory,
                          ProductRepository productRepository,
                          TransactionDetailRepository detailRepository) {
        this.factory = factory;
        this.productRepository = productRepository;
        this.detailRepository = detailRepository;
    }

    public void processTransaction(Transaction transaction, String methodType) {

        PriceCalculator calculator = new TaxDecorator(new BasePriceCalculator());
        Double finalAmount = calculator.calculate(transaction.getMontoTotal().doubleValue());

        PaymentProcessor processor = factory.getPaymentProcessor(methodType);
        boolean success = processor.processPayment(finalAmount);

        TransactionContext context = new TransactionContext();

        PaymentEventManager eventManager = new PaymentEventManager();
        eventManager.subscribe(new ReceiptGenerator());

        if (success) {
            context.setState(new PaidState(productRepository, detailRepository));
            context.process(transaction);
            eventManager.notifyObservers(transaction);
        } else {
            context.setState(new FailedState());
            context.process(transaction);
        }
    }
}