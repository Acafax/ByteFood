package org.example.dtos.stockItemDictionary;

import org.example.models.UnitType;

import java.math.BigDecimal;

public record StockItemDictionaryWithoutSemiProductDTO(
        Long id,
        String name,
        BigDecimal price,
        UnitType unit,
        Long restaurantId,
        BigDecimal multipleOfSemiProduct
) {
}
