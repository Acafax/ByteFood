package org.example.dtos.product;

import jakarta.validation.Valid;
import org.example.dtos.semiProducts.CreateProductSemiProductsDto;
import org.example.util.annotation.AtLeastOneNotNull;
import org.example.util.annotation.NullOrNotEmpty;
import org.example.util.annotation.NullOrValid;

import java.math.BigDecimal;
import java.util.Set;

@AtLeastOneNotNull(message = "At least one field must be provided for patch operation")
public record PatchProductDTO(
        @NullOrValid
        String name,

        @NullOrValid
        String category,

        @NullOrValid
        BigDecimal price,

        @NullOrNotEmpty(message = "Components list cannot be empty if provided")
        @Valid
        Set<CreateProductSemiProductsDto> productsSemiProducts
) {
}
