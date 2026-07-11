package supermercado.pagos.observer;

import supermercado.pagos.model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class PaymentEventManager {

    private final List<PaymentObserver> observers = new ArrayList<>();

    public void subscribe(PaymentObserver observer) {
        observers.add(observer);
    }

    public void unsubscribe(PaymentObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(Transaction transaction) {
        for (PaymentObserver observer : observers) {
            observer.update(transaction);
        }
    }
}