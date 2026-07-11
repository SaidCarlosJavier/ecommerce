package supermercado.pagos.state;

import supermercado.pagos.model.Transaction;

public interface TransactionState {
    void handle(TransactionContext context, Transaction transaction);
}