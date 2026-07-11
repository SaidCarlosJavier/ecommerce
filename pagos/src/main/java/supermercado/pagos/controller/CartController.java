package supermercado.pagos.controller;

import org.springframework.web.bind.annotation.*;
import supermercado.pagos.dto.CartDto;
import supermercado.pagos.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{userId}")
    public CartDto getCart(@PathVariable Long userId) {
        return cartService.getCartByUser(userId);
    }

    @PostMapping("/{userId}/add")
    public CartDto addProduct(@PathVariable Long userId,
                           @RequestParam Long productId,
                           @RequestParam Integer quantity) {
        return cartService.addProduct(userId, productId, quantity);
    }

    @DeleteMapping("/{userId}/remove")
    public CartDto removeProduct(@PathVariable Long userId,
                              @RequestParam Long productId) {
        return cartService.removeProduct(userId, productId);
    }

    @DeleteMapping("/{userId}/clear")
    public void clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
    }
}
