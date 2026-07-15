package supermercado.pagos.service;

import supermercado.pagos.dto.CartDto;

public interface CartService {
    void agregarProducto(Long userId, Long productId, Integer quantity);
    CartDto obtenerCarrito(Long userId);
    void limpiarCarrito(Long userId);
}