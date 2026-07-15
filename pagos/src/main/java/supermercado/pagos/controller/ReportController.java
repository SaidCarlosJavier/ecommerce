package supermercado.pagos.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import supermercado.pagos.model.Transaction;
import supermercado.pagos.repository.TransactionRepository;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReportController {

    private final TransactionRepository transactionRepository;

    @GetMapping("/reports")
    public ResponseEntity<?> getReportesSistemas() {
        // Simulación de protección Proxy estructural:
        // Si requieres simular el error 403 para probar el letrero rojo del JS, desenta esta línea:
        // if(true) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        List<Transaction> todasLasVentas = transactionRepository.findAll();
        return ResponseEntity.ok(todasLasVentas);
    }
}