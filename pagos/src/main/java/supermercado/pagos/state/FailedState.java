package supermercado.pagos.state;

import supermercado.pagos.model.Transaction;
import supermercado.pagos.repository.TransactionRepository;

public class FailedState implements TransactionState {

    private final TransactionRepository transactionRepository;

    public FailedState(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void handle(TransactionContext context, Transaction transaction) {
        // FIX: antes esto no se guardaba y la transacción quedaba "PENDIENTE" para siempre
        transaction.setEstado("FALLIDO");
        transactionRepository.save(transaction);

        System.out.println("Transacción FALLIDA");
    }
}
