package org.example.dtos.semiProducts;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.example.models.Subcategory;
import org.example.models.UnitType;

import java.math.BigDecimal;

public record CreateSemiProductDto(
        @NotBlank(message = "Name cannot be blank")
        String name,

        @NotNull(message = "Carbohydrate cannot be null")
        @PositiveOrZero(message = "Carbohydrate cannot be negative")
        BigDecimal carbohydrate,

        @NotNull(message = "Fat cannot be null")
        @PositiveOrZero(message = "Fat cannot be negative")
        BigDecimal fat,

        @NotNull(message = "Protein cannot be null")
        @PositiveOrZero(message = "Protein cannot be negative")
        BigDecimal protein,

        @NotNull(message = "Unit type cannot be null")
        UnitType unit,

        @NotNull(message = "Subcategory cannot be null")
        Subcategory subcategory,

        @NotNull(message = "Minimal stock quantity cannot be null")
        @PositiveOrZero(message = "Minimal stock quantity cannot be negative")
        BigDecimal minimalStockQuantity

) {
}
