package org.example.dtos.stock;

import org.example.dtos.semiProducts.SemiProductDTO;
import org.example.dtos.semiProducts.SemiProductForStockDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StockItemDTO(
        Long id,
        BigDecimal purchasePrice,
        BigDecimal quantity,
        LocalDateTime expirationDate,
        SemiProductForStockDTO semiProductDTO,
        Long restaurantId
) {
}
