package supermercado.pagos.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import supermercado.pagos.dto.CartDto;
import supermercado.pagos.dto.CartItemDto;
import supermercado.pagos.model.Product;
import supermercado.pagos.model.Transaction;
import supermercado.pagos.model.TransactionDetail;
import supermercado.pagos.model.User;
import supermercado.pagos.repository.ProductRepository;
import supermercado.pagos.repository.TransactionDetailRepository;
import supermercado.pagos.repository.TransactionRepository;
import supermercado.pagos.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionDetailRepository transactionDetailRepository;

    public Transaction createPendingTransaction(Long userId, CartDto cartDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Transaction transaction = Transaction.builder()
                .fecha(LocalDateTime.now())
                .montoSubtotal(java.math.BigDecimal.valueOf(cartDto.getTotalAmount()))
                .impuestos(java.math.BigDecimal.ZERO)
                .montoTotal(java.math.BigDecimal.valueOf(cartDto.getTotalAmount()))
                .metodoPago("PENDING_PAYMENT")
                .estado("PENDIENTE")
                .user(user)
                .build();

        transaction = transactionRepository.save(transaction);

        for (CartItemDto item : cartDto.getItems()) {

            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            TransactionDetail detail = TransactionDetail.builder()
                    .transaction(transaction)
                    .product(product)
                    .cantidad(item.getQuantity())
                    .precioUnitarioHistorico(
                            java.math.BigDecimal.valueOf(item.getUnitPrice())
                    )
                    .subtotal(
                            java.math.BigDecimal.valueOf(item.getSubtotal())
                    )
                    .build();

            transactionDetailRepository.save(detail);
        }

        return transaction;
    }
}