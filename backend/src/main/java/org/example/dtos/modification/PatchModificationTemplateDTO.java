package org.example.dtos.modification;

import org.example.models.Subcategory;
import org.example.util.annotation.AtLeastOneNotNull;
import org.example.util.annotation.NullOrValid;

import java.math.BigDecimal;

@AtLeastOneNotNull(message = "At least one field must be provided for patch operation")
public record PatchModificationTemplateDTO(
        @NullOrValid
        String name,

        @NullOrValid
        BigDecimal price,

        @NullOrValid
        Subcategory subcategory,

        @NullOrValid
        Long semiProductId
) {
}
