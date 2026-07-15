package supermercado.pagos.service;

import supermercado.pagos.model.Transaction;

public interface CheckoutFacade {
    Transaction realizarPago(Long userId, String metodoPago);
}