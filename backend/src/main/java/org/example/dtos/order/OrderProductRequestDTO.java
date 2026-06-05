package org.example.dtos.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.example.dtos.modification.ModificationRequestDTO;

import java.math.BigDecimal;
import java.util.List;

public record OrderProductRequestDTO(
        @NotNull(message = "Quantity cannot be null")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity,

        @NotNull(message = "Price cannot be null")
        @PositiveOrZero(message = "Price cannot be negative")
        BigDecimal price,

        String description,

        @NotNull(message = "Product ID cannot be null")
        @Min(value = 1, message = "Product ID must be greater than 0")
        Long productId,

        List<ModificationRequestDTO> modifications
) {
}
