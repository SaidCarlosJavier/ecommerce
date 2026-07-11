package supermercado.pagos.state;


import supermercado.pagos.model.Transaction;

public class FailedState implements TransactionState {

    @Override
    public void handle(TransactionContext context, Transaction transaction) {
        System.out.println("Transacción FALLIDA");
    }
}