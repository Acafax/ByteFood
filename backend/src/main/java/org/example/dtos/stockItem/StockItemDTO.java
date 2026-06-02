package org.example.dtos.stockItem;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.example.models.SemiProduct;
import org.example.models.Stock;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StockItemDTO(
    Long id,

    BigDecimal purchasePrice,

    BigDecimal quantity,

    LocalDateTime expirationDate,

    Long semiProductId,

    Long restaurantId
) {
}
