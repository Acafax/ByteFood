package org.example.dtos;

import java.math.BigDecimal;

public record CreateProductSemiProductsDto(Long semiProductId, BigDecimal quantity) {
}
