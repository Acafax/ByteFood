package org.example.dtos.semiProducts;

import org.example.models.UnitType;

import java.math.BigDecimal;

public record SemiProductForStockDTO(
    Long id,
    String name,
    UnitType unit,
    BigDecimal minimalStockQuantity,
    Long restaurantId
)
{ }
