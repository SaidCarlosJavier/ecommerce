package supermercado.pagos.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import supermercado.pagos.model.Transaction;
import supermercado.pagos.model.User;
import supermercado.pagos.proxy.ReportService;
import supermercado.pagos.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final ReportService reportService;
    private final UserRepository userRepository;

    public AdminController(ReportService reportService, UserRepository userRepository) {
        this.reportService = reportService;
        this.userRepository = userRepository;
    }

    // Antes: recibía el User completo por @RequestBody en un GET (no funciona con fetch()).
    // Ahora: el usuario autenticado se obtiene del JWT (puesto en el SecurityContext
    // por JwtAuthFilter), y el proxy sigue validando el rol como ya lo hacía.
    @GetMapping("/sales")
    public List<Transaction> getSalesByCashier(@RequestParam Long cashierId) {
        Long requestingUserId = (Long) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        User requestingUser = userRepository.findById(requestingUserId)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        return reportService.getSalesByCashier(cashierId, requestingUser);
    }
}
