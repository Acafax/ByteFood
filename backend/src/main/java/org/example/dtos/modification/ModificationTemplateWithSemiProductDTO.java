package org.example.dtos.modification;

import org.example.dtos.semiProducts.SemiProductDTO;
import org.example.models.Subcategory;

import java.math.BigDecimal;

public record ModificationTemplateWithSemiProductDTO(
        Long id,
        String name,
        BigDecimal price,
        Subcategory subcategory,
        SemiProductDTO semiProductDTO,
        Long restaurantId
) {
}
