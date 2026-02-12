package org.example.dtos.combo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import org.example.util.NullOrNotEmpty;
import org.example.util.NullOrValid;

import java.math.BigDecimal;
import java.util.List;

public record PatchComboDTO(
        @NullOrValid
        String name,

        @DecimalMin(value = "0.01", message = "Price have to be higher then 0")
        BigDecimal price,

        @NullOrNotEmpty(message = "Components list cannot be empty if provided")
        @Valid
        List<CreateComboProductDTO> components
) {
}
