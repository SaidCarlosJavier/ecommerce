package supermercado.pagos.dto;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CartItemDto {

    private Long productId;
    private String productName;
    private Double unitPrice;
    private Integer quantity;

    public Double getSubtotal() {
        return unitPrice * quantity;
    }

}
