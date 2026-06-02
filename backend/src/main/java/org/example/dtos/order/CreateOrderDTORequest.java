package org.example.dtos.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.example.dtos.combo.ComboRequestDTO;
import org.hibernate.validator.constraints.time.DurationMin;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public record CreateOrderDTORequest(

        @NotNull(message = "Order time cannot be null")
        LocalDateTime orderTime,

        @NotNull(message = "Preparation time cannot be null")
        @DurationMin(millis = 1, message = "Preparation time must be greater than 0")
        Duration preparationTime,

        @NotNull(message = "Price cannot be null")
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        BigDecimal price,

        @NotNull(message = "Products list cannot be null")
        @Valid
        List<OrderProductRequestDTO> products,

        @NotNull(message = "Combos list cannot be null")
        @Valid
        List<ComboRequestDTO> combos
) {
}
