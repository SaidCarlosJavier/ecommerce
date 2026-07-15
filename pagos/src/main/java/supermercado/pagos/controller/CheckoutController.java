package supermercado.pagos.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import supermercado.pagos.model.Transaction;
import supermercado.pagos.service.CheckoutFacade;

import java.util.Map;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CheckoutController {

    private final CheckoutFacade checkoutFacade;

    @PostMapping("/{userId}/pay")
    public ResponseEntity<?> procesar(@PathVariable Long userId, @RequestParam String method) {
        try {
            Transaction t = checkoutFacade.realizarPago(userId, method);
            return ResponseEntity.ok(t);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}