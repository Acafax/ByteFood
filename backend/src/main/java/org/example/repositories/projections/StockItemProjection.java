package org.example.repositories.projections;

import org.example.models.UnitType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface StockItemProjection {

    Long getId();
    BigDecimal getPurchasePrice();
    BigDecimal getQuantity();
    LocalDateTime getExpirationDate();
    Long getRestaurantId();

    // SemiProduct fields
    Long   getSemiProductId();
    String getSemiProductName();
    UnitType getSemiProductUnit();
    BigDecimal getSemiProductMinimalStockQuantity();



}
