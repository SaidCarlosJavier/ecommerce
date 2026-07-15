package supermercado.pagos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String numeroTicket;

    private LocalDateTime fecha;

    private BigDecimal montoSubtotal;
    private BigDecimal impuestos;
    private BigDecimal montoTotal;

    private String metodoPago;

    @NotNull
    private String estado;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "transaction",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private List<TransactionDetail> detalles;
}
