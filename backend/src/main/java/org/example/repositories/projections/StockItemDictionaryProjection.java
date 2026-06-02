package org.example.repositories.projections;

import org.example.models.SemiProduct;
import org.example.models.UnitType;
import org.example.dtos.semiProducts.SemiProductForStockDTO;

import java.math.BigDecimal;

public interface StockItemDictionaryProjection {
    Long getId();
    String getName();
    BigDecimal getPrice();
    BigDecimal getMultipleOfSemiProduct();
    UnitType getUnit();
    Long getRestaurantId();
    SemiProduct getSemiProduct();

}
