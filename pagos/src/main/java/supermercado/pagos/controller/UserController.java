package supermercado.pagos.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import supermercado.pagos.model.Transaction;
import supermercado.pagos.repository.TransactionRepository;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final TransactionRepository transactionRepository;

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<Transaction>> obtenerHistorialCompras(@PathVariable Long userId) {
        List<Transaction> ordenes = transactionRepository.findByUserIdOrderByFechaDesc(userId);
        return ResponseEntity.ok(ordenes);
    }
}