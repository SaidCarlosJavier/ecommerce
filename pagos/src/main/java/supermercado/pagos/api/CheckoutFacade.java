package supermercado.pagos.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import supermercado.pagos.discount.BasePriceCalculator;
import supermercado.pagos.discount.PriceCalculator;
import supermercado.pagos.discount.TaxDecorator;
import supermercado.pagos.dto.CartDto;
import supermercado.pagos.model.Transaction;
import supermercado.pagos.model.User;
import supermercado.pagos.observer.PaymentEventManager;
import supermercado.pagos.observer.ReceiptGenerator;
import supermercado.pagos.payment.PaymentProcessor;
import supermercado.pagos.payment.PaymentProcessorFactory;
import supermercado.pagos.repository.ProductRepository;
import supermercado.pagos.repository.TransactionDetailRepository;
import supermercado.pagos.repository.TransactionRepository;
import supermercado.pagos.repository.UserRepository;
import supermercado.pagos.service.CartService;
import supermercado.pagos.state.FailedState;
import supermercado.pagos.state.PaidState;
import supermercado.pagos.state.TransactionContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class CheckoutFacade {

    @Autowired
    private UserRepository userRepository; // ¡Asegúrate de que esta inyección exista!
    @Autowired
    private CartService cartService;
    @Autowired
    private PaymentProcessorFactory paymentFactory;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private TransactionDetailRepository detailRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    public CheckoutFacade(CartService cartService,
                          PaymentProcessorFactory paymentFactory,
                          ProductRepository productRepository,
                          TransactionDetailRepository detailRepository) {
        this.cartService = cartService;
        this.paymentFactory = paymentFactory;
        this.productRepository = productRepository;
        this.detailRepository = detailRepository;

    }

    public Transaction processCheckout(Long userId, String paymentMethod) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        CartDto cart = cartService.getCart();

        Transaction transaction = Transaction.builder()
                .user(user)
                .fecha(LocalDateTime.now())
                .montoTotal(BigDecimal.valueOf(cart.getTotalAmount()))
                .estado("PENDIENTE")
                .metodoPago(paymentMethod)
                .build();

        PriceCalculator calculator = new TaxDecorator(new BasePriceCalculator());
        Double finalAmount = calculator.calculate(transaction.getMontoTotal().doubleValue());
        transaction.setMontoTotal(BigDecimal.valueOf(finalAmount));

        PaymentProcessor processor = paymentFactory.getPaymentProcessor(paymentMethod);
        boolean success = processor.processPayment(finalAmount);

        TransactionContext context = new TransactionContext();
        PaymentEventManager eventManager = new PaymentEventManager();
        eventManager.subscribe(new ReceiptGenerator());

        if (success) {
            context.setState(new PaidState(productRepository, detailRepository));
            transaction.setEstado("COMPLETADO");
            transaction = transactionRepository.save(transaction);
            context.process(transaction);
            eventManager.notifyObservers(transaction);
            cartService.clearCart(userId);
        } else {
            context.setState(new FailedState());
            transaction.setEstado("FALLIDO");
            transaction = transactionRepository.save(transaction);
            context.process(transaction);
        }

        return transaction;
    }
}