package supermercado.pagos.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String nombre;

    private String descripcion;

    @Positive
    private BigDecimal precio;

    @Min(0)
    private Integer stock;

    private String imagenUrl;

    private Boolean activo;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}