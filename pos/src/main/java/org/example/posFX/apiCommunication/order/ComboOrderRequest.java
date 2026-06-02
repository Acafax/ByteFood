package org.example.posFX.apiCommunication.order;

import java.math.BigDecimal;
import java.util.List;

public record ComboOrderRequest(
        String name,
        Integer quantity,
        BigDecimal price,
        Long comboId,
        List<OrderProductRequest> comboProducts
) {
}
