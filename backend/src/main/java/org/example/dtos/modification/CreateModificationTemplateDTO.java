package org.example.dtos.modification;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.Optional;

public record CreateModificationTemplateDTO(
        @NotBlank(message = "Name cant be blank")
        String name,

        @DecimalMin(value = "0.01", message = "Price have to be higher then 0")
        BigDecimal price,

        @NotBlank(message = "Category cant be blank")
        String category,

        @Nullable
        Long semiProductId
) {
}
