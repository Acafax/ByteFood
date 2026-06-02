package org.example.dtos.stockItemDictionary;

import org.example.dtos.semiProducts.SemiProductDTO;
import org.example.dtos.semiProducts.SemiProductForStockDTO;
import org.example.models.UnitType;

import java.math.BigDecimal;

public record StockItemDictionaryDTO(
    Long id,
    String name,
    BigDecimal price,
    UnitType unit,
    Long restaurantId,
    BigDecimal multipleOfSemiProduct,
    SemiProductForStockDTO semiProductDTO

) {
}
