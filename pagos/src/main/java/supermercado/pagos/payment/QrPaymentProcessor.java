package supermercado.pagos.payment;

public class QrPaymentProcessor implements PaymentProcessor {

    @Override
    public boolean processPayment(Double amount) {
        PaymentGatewayConnection connection = PaymentGatewayConnection.getInstance();
        return connection.authenticateAndProcess(amount);
    }
}