package org.example.dtos.modification;

import jakarta.annotation.Nullable;
import org.example.models.Subcategory;

import java.math.BigDecimal;
import java.util.Optional;

public record ModificationTemplateDto(
        Long id,
        String name,
        BigDecimal price,

        Subcategory subcategory,

        @Nullable
        Long semiProductId,

        Long restaurantId
) {
}
