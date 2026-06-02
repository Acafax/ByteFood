package org.example.dtos.stockItemDictionary;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.example.models.UnitType;

import java.math.BigDecimal;

public record CreateStockItemDictionaryDTO(

        @NotBlank(message = "Name cannot be blank")
        String name,

        @NotNull(message = "Price cannot be null")
        @PositiveOrZero(message = "Price cannot be negative")
        BigDecimal price,

        @NotNull(message = "Unit type cannot be null")
        UnitType unit,

        @NotNull(message = "Multiple of semi product cannot be null")
        @PositiveOrZero(message = "Multiple of semi product cannot be negative")
        BigDecimal multipleOfSemiProduct,

        @NotNull(message = "Semi product ID cannot be null")
        Long semiProductID

) {
}
