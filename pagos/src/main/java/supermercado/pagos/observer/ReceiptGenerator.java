package supermercado.pagos.observer;

import supermercado.pagos.model.Transaction;

import java.time.format.DateTimeFormatter;

public class ReceiptGenerator implements PaymentObserver {

    @Override
    public void update(Transaction transaction) {
        System.out.println("----- RECIBO -----");
        System.out.println("Cliente: " + transaction.getUser());
        System.out.println("Monto: " + transaction.getMontoTotal());
        System.out.println("Fecha: " + transaction.getFecha().format(DateTimeFormatter.ISO_DATE_TIME));
        System.out.println("------------------");
    }
}