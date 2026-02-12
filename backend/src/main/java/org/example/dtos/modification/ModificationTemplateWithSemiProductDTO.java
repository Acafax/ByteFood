package org.example.dtos.modification;

import org.example.dtos.semiProducts.SemiProductDTO;

import java.math.BigDecimal;

public record ModificationTemplateWithSemiProductDTO(
        Long id,
        String name,
        BigDecimal price,
        String category,
        SemiProductDTO semiProductDTO,
        Long restaurantId
) {
}
