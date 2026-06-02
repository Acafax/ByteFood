package org.example.dtos.combo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import org.example.util.annotation.AtLeastOneNotNull;
import org.example.util.annotation.NullOrNotEmpty;
import org.example.util.annotation.NullOrValid;

import java.math.BigDecimal;
import java.util.List;

@AtLeastOneNotNull(message = "At least one field must be provided for patch operation")
public record PatchComboDTO(
        @NullOrValid
        String name,

        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        BigDecimal price,

        @NullOrNotEmpty(message = "Components list cannot be empty if provided")
        @Valid
        List<CreateComboProductDTO> components
) {
}
