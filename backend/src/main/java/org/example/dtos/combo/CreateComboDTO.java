package org.example.dtos.combo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record CreateComboDTO (
        @NotBlank(message = "Combo name cannot be blank")
        String name,

        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        @NotNull(message = "Price cannot be null")
        BigDecimal price,

        @NotEmpty(message = "Combo must have components")
        @Valid
        List<CreateComboProductDTO> components) {
}
