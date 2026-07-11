package supermercado.pagos.payment;


public class CashPaymentProcessor implements PaymentProcessor {

    @Override
    public boolean processPayment(Double amount) {
        System.out.println("Pago en efectivo recibido: " + amount);
        return true;
    }
}