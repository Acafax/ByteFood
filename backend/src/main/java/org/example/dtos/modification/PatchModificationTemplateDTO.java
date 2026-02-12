package org.example.dtos.modification;

import jakarta.validation.constraints.DecimalMin;
import org.example.util.NullOrValid;

import java.math.BigDecimal;

public record PatchModificationTemplateDTO(
    @NullOrValid
    String name,

    @DecimalMin(value = "0.01", message = "Price have to be higher then 0")
    BigDecimal price,

    @NullOrValid
    String category,

    @NullOrValid
    Long semiProductId
) {
}
