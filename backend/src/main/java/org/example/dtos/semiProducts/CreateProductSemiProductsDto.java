package org.example.dtos.semiProducts;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateProductSemiProductsDto(
        @NotNull(message = "Semi-product ID cannot be null")
        @Min(value = 1, message = "Semi-product ID must be greater than 0")
        Long semiProductId,

        @NotNull(message = "Quantity cannot be null")
        @Positive(message = "Quantity must be positive")
        BigDecimal quantity
) {
}
