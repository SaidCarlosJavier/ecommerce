package supermercado.pagos.proxy;

import org.springframework.stereotype.Service;
import supermercado.pagos.model.Transaction;
import supermercado.pagos.model.User;
import supermercado.pagos.repository.TransactionRepository;

import java.util.List;

@Service
public class RealReportService implements ReportService {

    private final TransactionRepository transactionRepository;

    public RealReportService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<Transaction> getSalesByCashier(Long cashierId, User requestingUser) {
        return transactionRepository.findByUserId(cashierId);
    }
}