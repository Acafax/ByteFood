package org.example.repositories.projections;

import org.example.models.UnitType;

import java.math.BigDecimal;

public interface SemiProductProjection {
    Long getId();
    String getName();
    BigDecimal getPrice();
    BigDecimal getCarbohydrate();
    BigDecimal getFat();
    BigDecimal getProtein();
    UnitType getUnitType();
    Long getMinimalStockQuantity();
    Long getRestaurantId();
    Long getDeletedAt();
}
