package org.example.posFX.apiCommunication.order;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public record CreateOrderRequest(
        LocalDateTime orderTime,
        Duration preparationTime,
        BigDecimal price,
        List<OrderProductRequest> products,
        List<ComboOrderRequest> combos
) {
}
