package org.example.dtos.combo;

import java.math.BigDecimal;

public record DeletedComboDto(
        String name,
        BigDecimal price
) {
}
