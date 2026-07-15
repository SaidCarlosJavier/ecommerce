package supermercado.pagos.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import supermercado.pagos.dto.CartDto;
import supermercado.pagos.dto.CartItemDto;
import supermercado.pagos.model.Product;
import supermercado.pagos.repository.ProductRepository;
import supermercado.pagos.service.CartService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final ProductRepository productRepository;
    // Hilo-seguro para gestionar los carritos de todos los usuarios activos
    private final Map<Long, CartDto> carritosMemoria = new ConcurrentHashMap<>();

    @Override
    public void agregarProducto(Long userId, Long productId, Integer quantity) {
        Product producto = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (producto.getStock() < quantity) {
            throw new RuntimeException("Stock insuficiente para añadir al carrito");
        }

        CartDto carrito = carritosMemoria.computeIfAbsent(userId, k -> new CartDto());

        // Verificar si el ítem ya existe en el carrito
        CartItemDto itemExistente = carrito.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElse(null);

        if (itemExistente != null) {
            itemExistente.setQuantity(itemExistente.getQuantity() + quantity);
        } else {
            CartItemDto nuevoItem = CartItemDto.builder()
                    .productId(producto.getId())
                    .productName(producto.getNombre())
                    .unitPrice(producto.getPrecio().doubleValue())
                    .quantity(quantity)
                    .build();
            carrito.getItems().add(nuevoItem);
        }
    }

    @Override
    public CartDto obtenerCarrito(Long userId) {
        return carritosMemoria.getOrDefault(userId, new CartDto());
    }

    @Override
    public void limpiarCarrito(Long userId) {
        carritosMemoria.remove(userId);
    }
}