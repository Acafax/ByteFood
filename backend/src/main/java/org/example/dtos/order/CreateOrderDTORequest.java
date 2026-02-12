package org.example.dtos.order;

import org.example.dtos.combo.ComboRequestDTO;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public record CreateOrderDTORequest(
        LocalDateTime orderTime,
        Duration preparationTime,
        BigDecimal price,
        List<OrderProductRequestDTO> products,
        List<ComboRequestDTO> combos) {
}

