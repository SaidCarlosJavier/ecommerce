package supermercado.pagos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import supermercado.pagos.dto.CartDto;
import supermercado.pagos.dto.CartItemDto;
import supermercado.pagos.model.Product;
import supermercado.pagos.repository.ProductRepository;

import java.util.HashMap;
import java.util.Map;

@Service
public class CartService {

    private final Map<Long, CartItemDto> cartItems = new HashMap<>();

    public void addItem(Product product, int quantity) {
        CartItemDto existingItem = cartItems.get(product.getId());

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            CartItemDto newItem = CartItemDto.builder()
                    .productId(product.getId())
                    .productName(product.getNombre())
                    .unitPrice(product.getPrecio().doubleValue())
                    .quantity(quantity)
                    .build();

            cartItems.put(product.getId(), newItem);
        }
    }

    public void removeItem(Long productId) {
        cartItems.remove(productId);
    }

    public CartDto getCart() {
        return CartDto.builder()
                .items(new java.util.ArrayList<>(cartItems.values()))
                .build();
    }

    public CartDto getCartByUser(Long userId) {
        return getCart();
    }

    public void clearCart(Long userId) {
        cartItems.clear();
    }
    @Autowired
    private ProductRepository productRepository;


    public CartDto addProduct(Long userId, Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        addItem(product, quantity);
        return getCart();
    }

    // Este es el método que tu controlador llamará
    public CartDto removeProduct(Long userId, Long productId) {
        removeItem(productId);
        return getCart();
    }
}