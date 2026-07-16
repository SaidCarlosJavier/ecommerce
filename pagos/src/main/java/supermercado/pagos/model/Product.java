package supermercado.pagos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
    @JsonProperty("name")
    private String nombre;

    private String descripcion;

    @Positive
    @JsonProperty("price")
    private BigDecimal precio;

    @Min(0)
    private Integer stock;

    private String imagenUrl;

    private Boolean activo;

    @JsonIgnore // 🔥 EVITA ERROR JSON
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}