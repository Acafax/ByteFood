package org.example.dtos;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ModificationRequestDTO(
        @NotBlank(message = "Modification name cannot be blank")
        String name,

        @NotNull(message = "Quantity cannot be null")
        @DecimalMin(value = "1", message = "Quantity must be at least 1")
        BigDecimal quantity,

        @NotNull(message = "Price cannot be null")
        @PositiveOrZero(message = "Price cannot be negative")
        BigDecimal price,

        @NotNull(message = "Modification template ID cannot be null")
        @Min(value = 1, message = "Modification template ID must be greater than 0")
        Long modificationTemplateId
) {
}
