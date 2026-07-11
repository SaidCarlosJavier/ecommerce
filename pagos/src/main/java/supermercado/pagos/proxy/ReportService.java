package supermercado.pagos.proxy;

import supermercado.pagos.model.Transaction;
import supermercado.pagos.model.User;

import java.util.List;

public interface ReportService {
    List<Transaction> getSalesByCashier(Long cashierId, User requestingUser);
}
