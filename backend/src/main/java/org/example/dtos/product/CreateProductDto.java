package org.example.dtos.product;

import org.example.dtos.CreateProductSemiProductsDto;
import org.example.models.Product;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Set;

public record CreateProductDto(
        @NotBlank(message = "Name cant be blank")
        String name,

        @NotBlank(message = "Category cant be blank")
        String category,

        @DecimalMin(value = "0.01", message = "Price have to be higher then 0")
        BigDecimal price,

        Set<CreateProductSemiProductsDto> productsSemiProducts) {
}
