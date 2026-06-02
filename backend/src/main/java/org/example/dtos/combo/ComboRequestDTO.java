package org.example.dtos.combo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.example.dtos.order.OrderProductRequestDTO;

import java.math.BigDecimal;
import java.util.List;

public record ComboRequestDTO(
        @NotBlank(message = "Combo name cannot be blank")
        String name,

        @NotNull(message = "Quantity cannot be null")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity,

        @NotNull(message = "Price cannot be null")
        @PositiveOrZero(message = "Price cannot be negative")
        BigDecimal price,

        @NotNull(message = "Combo ID cannot be null")
        @Min(value = 1, message = "Combo ID must be greater than 0")
        Long comboId,

        @NotEmpty(message = "Combo must have products")
        @Valid
        List<OrderProductRequestDTO> comboProducts
) {
}
