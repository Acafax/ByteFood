package org.example.dtos.modification;

import jakarta.annotation.Nullable;

import java.math.BigDecimal;
import java.util.Optional;

public record ModificationTemplateDto(
        Long id,
        String name,
        BigDecimal price,
        String category,

        @Nullable
        Long semiProductId,

        Long restaurantId
) {
}
