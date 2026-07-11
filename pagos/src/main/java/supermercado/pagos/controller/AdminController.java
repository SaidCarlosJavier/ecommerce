package supermercado.pagos.controller;

import org.springframework.web.bind.annotation.*;
import supermercado.pagos.model.Transaction;
import supermercado.pagos.model.User;
import supermercado.pagos.proxy.ReportService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ReportService reportService;

    public AdminController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/sales")
    public List<Transaction> getSalesByCashier(@RequestParam Long cashierId,
                                               @RequestBody User requestingUser) {
        return reportService.getSalesByCashier(cashierId, requestingUser);
    }
}
