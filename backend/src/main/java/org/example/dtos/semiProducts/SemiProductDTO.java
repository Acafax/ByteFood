package org.example.dtos.semiProducts;

import org.example.models.UnitType;

import java.math.BigDecimal;

public record SemiProductDTO(
        Long id,
        String name,
        BigDecimal carbohydrate,
        BigDecimal fat,
        BigDecimal protein,
        UnitType unit,
        Long restaurantId
){
}
