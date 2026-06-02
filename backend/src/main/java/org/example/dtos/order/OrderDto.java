package org.example.dtos.order;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.example.dtos.combo.OrderComboDTO;


public record OrderDto(
        Long id,
        LocalDateTime orderTime,
        Duration preparationTime,
        BigDecimal price,
        List<OrderProductDTO> products,
        List<OrderComboDTO> combos
) {
}
