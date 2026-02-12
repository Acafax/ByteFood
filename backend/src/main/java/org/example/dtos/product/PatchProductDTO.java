package org.example.dtos.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.example.dtos.CreateProductSemiProductsDto;
import org.example.util.NullOrValid;

import java.math.BigDecimal;
public record PatchProductDTO (
        @NullOrValid
        String name,

        @NullOrValid
        String category,

        @DecimalMin(value = "0.01", message = "Price have to be higher then 0")
        BigDecimal price
){}
