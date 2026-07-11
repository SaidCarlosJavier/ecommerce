package supermercado.pagos.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDto {

    @Builder.Default
    private List<CartItemDto> items = new ArrayList<>();

    public Double getTotalAmount() {
        return items.stream()
                .mapToDouble(CartItemDto::getSubtotal)
                .sum();
    }

}

