package org.example.dtos.semiProducts;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import org.example.models.UnitType;

import java.math.BigDecimal;

public record CreateSemiProductDto(
        @NotBlank(message = "Name cant be blank")
        String name,

        @DecimalMin(value = "0.001", message = "Macro have to be higher then 0")
        BigDecimal carbohydrate,

        @DecimalMin(value = "0.001", message = "Macro have to be higher then 0")
        BigDecimal fat,

        @DecimalMin(value = "0.001", message = "Macro have to be higher then 0")
        BigDecimal protein,

        UnitType unit
){
}
