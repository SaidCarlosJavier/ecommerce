package supermercado.pagos.payment;

import org.springframework.stereotype.Component;

@Component
public class PaymentProcessorFactory {

    public PaymentProcessor getPaymentProcessor(String methodType) {

        switch (methodType.toUpperCase()) {
            case "CARD":
                return new CardPaymentProcessor();
            case "QR":
                return new QrPaymentProcessor();
            case "CASH":
                return new CashPaymentProcessor();
            default:
                throw new IllegalArgumentException("Método de pago no soportado");
        }
    }
}