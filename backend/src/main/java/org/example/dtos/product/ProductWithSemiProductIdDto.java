package org.example.dtos.product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record ProductWithSemiProductIdDto(Long id, String name, String category, BigDecimal price, Map<Long,BigDecimal> productSemiProductIdQuantity) {
}
