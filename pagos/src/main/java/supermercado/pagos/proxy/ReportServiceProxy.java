package supermercado.pagos.proxy;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import supermercado.pagos.model.Role;
import supermercado.pagos.model.Transaction;
import supermercado.pagos.model.User;

import java.util.List;

@Service
@Primary
public class ReportServiceProxy implements ReportService {

    private final RealReportService realReportService;

    public ReportServiceProxy(RealReportService realReportService) {
        this.realReportService = realReportService;
    }

    @Override
    public List<Transaction> getSalesByCashier(Long cashierId, User requestingUser) {
        if (requestingUser.getRole() != Role.ADMIN) {
            throw new SecurityException("Access denied");
        }
        return realReportService.getSalesByCashier(cashierId, requestingUser);
    }
}