package org.example.dtos.combo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateComboProductDTO(
        @NotNull(message = "Quantity cannot be null")
        @Min(value = 1, message = "Quantity have to be higher than 0")
        Integer quantity,

        @NotNull(message = "Product id cannot be null")
        Long productId) {
}
