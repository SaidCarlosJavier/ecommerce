package supermercado.pagos.observer;

import supermercado.pagos.model.Transaction;

public interface PaymentObserver {
    void update(Transaction transaction);
}