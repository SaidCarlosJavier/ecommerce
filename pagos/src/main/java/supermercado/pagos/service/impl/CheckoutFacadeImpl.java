package supermercado.pagos.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supermercado.pagos.dto.CartDto;
import supermercado.pagos.model.Transaction;
import supermercado.pagos.service.CartService;
import supermercado.pagos.service.CheckoutFacade;
import supermercado.pagos.service.PaymentService;
import supermercado.pagos.service.TransactionService;

@Service
@RequiredArgsConstructor
@Transactional
public class CheckoutFacadeImpl implements CheckoutFacade {

    private final CartService cartService;
    private final TransactionService transactionService;
    private final PaymentService paymentService;

    @Override
    public Transaction realizarPago(Long userId, String metodoPago) {

        // 1. Validar que haya algo que pagar
        CartDto carrito = cartService.obtenerCarrito(userId);
        if (carrito.getItems().isEmpty()) {
            throw new RuntimeException("No se puede procesar un pago con el carrito vacío");
        }

        // 2. Crear la transacción en estado PENDIENTE (todavía NO se toca el stock)
        Transaction pendiente = transactionService.createPendingTransaction(userId, carrito);

        // 3. Procesar el pago real: Factory (elige el procesador) + Decorator (calcula
        //    impuestos) + State (pasa a PAGADO o FALLIDO) + Observer (notifica el recibo).
        //    El stock SOLO se descuenta dentro de PaidState si el pago fue exitoso.
        Transaction resultado = paymentService.processTransaction(pendiente, metodoPago);

        // 4. El carrito solo se vacía si el pago fue confirmado.
        //    Si falló, el usuario conserva su carrito para reintentar.
        if ("PAGADO".equals(resultado.getEstado())) {
            cartService.limpiarCarrito(userId);
        }

        return resultado;
    }
}
