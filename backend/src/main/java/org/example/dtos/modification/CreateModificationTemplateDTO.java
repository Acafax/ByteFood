package org.example.dtos.modification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.example.models.Subcategory;
import org.example.util.annotation.NullOrValid;

import java.math.BigDecimal;

public record CreateModificationTemplateDTO(
        @NotBlank(message = "Name cannot be blank")
        String name,

        @PositiveOrZero(message = "Price cannot be negative")
        @NotNull(message = "Price cannot be null")
        BigDecimal price,

        @NotNull(message = "Subcategory cannot be null")
        Subcategory subcategory,

        // Can be null when the modification doesn't add a new physical semi-product
        // (example ingredient removal like "no ice", or special preparation instructions).
        @NullOrValid
        Long semiProductId
) {
}
