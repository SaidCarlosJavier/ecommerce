package supermercado.pagos.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private String imagenUrl;
    private Boolean activo;
}
