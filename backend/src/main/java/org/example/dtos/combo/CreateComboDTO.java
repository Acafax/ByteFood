package org.example.dtos.combo;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.math.BigDecimal;
import java.util.List;

public record CreateComboDTO (
        @NotBlank
        String name,

        @DecimalMin(value = "0.01", message = "Price have to be higher then 0")
        BigDecimal price,

        @NotEmpty(message = "Combo must have components")
        List<CreateComboProductDTO> components) {
}
