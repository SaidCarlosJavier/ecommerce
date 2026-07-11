package supermercado.pagos.state;


import supermercado.pagos.model.Transaction;

public class TransactionContext {

    private TransactionState currentState;

    public void setState(TransactionState state) {
        this.currentState = state;
    }

    public void process(Transaction transaction) {
        currentState.handle(this, transaction);
    }
}
