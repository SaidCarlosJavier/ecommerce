package supermercado.pagos.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import supermercado.pagos.dto.CartDto;
import supermercado.pagos.service.CartService;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CartController {

    private final CartService cartService;

    @PostMapping("/{userId}/add")
    public ResponseEntity<?> agregar(@PathVariable Long userId,
                                     @RequestParam Long productId,
                                     @RequestParam Integer quantity) {
        try {
            cartService.agregarProducto(userId, productId, quantity);
            return ResponseEntity.ok(Map.of("message", "Producto añadido"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{userId}/get")
    public ResponseEntity<CartDto> obtener(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.obtenerCarrito(userId));
    }
}