package org.example.dtos.stock;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StockItemDTO(
        Long id,
        BigDecimal purchasePrice,
        BigDecimal quantity,
        LocalDateTime expirationDate,
        StockItemDictionaryWithoutSemiProductDTO stockItemDictionary,
        Long restaurantId
) {
}
