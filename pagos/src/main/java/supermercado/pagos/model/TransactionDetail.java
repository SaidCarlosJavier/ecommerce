package supermercado.pagos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "transaction_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(1)
    private Integer cantidad;

    @Positive
    private BigDecimal precioUnitarioHistorico;

    private BigDecimal subtotal;

    @JsonIgnore // FIX: evita el ciclo infinito Transaction -> detalles -> transaction -> detalles...
    @ManyToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

}
