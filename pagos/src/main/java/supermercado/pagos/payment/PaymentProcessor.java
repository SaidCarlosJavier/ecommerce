package supermercado.pagos.payment;

public interface PaymentProcessor {
    boolean processPayment(Double amount);
}