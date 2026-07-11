package supermercado.pagos.payment;

public class PaymentGatewayConnection {

    private static volatile PaymentGatewayConnection instance;

    private PaymentGatewayConnection() {}

    public static PaymentGatewayConnection getInstance() {
        if (instance == null) {
            synchronized (PaymentGatewayConnection.class) {
                if (instance == null) {
                    instance = new PaymentGatewayConnection();
                }
            }
        }
        return instance;
    }

    public boolean authenticateAndProcess(Double amount) {
        System.out.println("Procesando pago seguro en pasarela externa: " + amount);
        return true;
    }
}