package org.example.dtos.stock;

import org.example.models.UnitType;

import java.math.BigDecimal;

public record StockItemDictionaryWithoutSemiProductDTO(
    Long id,
    String name,
    BigDecimal price,
    BigDecimal minimalStockQuantity,
    UnitType unit
    ) {
}
