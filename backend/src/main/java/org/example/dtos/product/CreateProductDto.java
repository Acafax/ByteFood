package org.example.dtos.product;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.example.dtos.semiProducts.CreateProductSemiProductsDto;

import java.math.BigDecimal;
import java.util.Set;

public record CreateProductDto(
        @NotBlank(message = "Name cannot be blank")
        String name,

        @NotBlank(message = "Category cannot be blank")
        String category,

        @NotNull(message = "Price cannot be null")
        @PositiveOrZero(message = "Price cannot be negative")
        BigDecimal price,

        @NotEmpty(message = "Product must have at least one semi-product")
        @Valid
        Set<CreateProductSemiProductsDto> productsSemiProducts
) {
}
