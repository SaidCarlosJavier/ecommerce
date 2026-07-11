package supermercado.pagos.payment;

public class CardPaymentProcessor implements PaymentProcessor {

    @Override
    public boolean processPayment(Double amount) {
        PaymentGatewayConnection connection = PaymentGatewayConnection.getInstance();
        return connection.authenticateAndProcess(amount);
    }
}