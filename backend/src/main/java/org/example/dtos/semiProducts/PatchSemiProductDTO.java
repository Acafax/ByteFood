package org.example.dtos.semiProducts;

import org.example.models.Subcategory;
import org.example.models.UnitType;
import org.example.util.annotation.AtLeastOneNotNull;
import org.example.util.annotation.NullOrValid;
import jakarta.validation.Valid;

import java.math.BigDecimal;

@AtLeastOneNotNull(message = "At least one field must be provided for patch operation")
public record PatchSemiProductDTO(

        @NullOrValid
        String name,

        @NullOrValid
        BigDecimal carbohydrate,

        @NullOrValid
        BigDecimal fat,

        @NullOrValid
        BigDecimal protein,

        UnitType unit,

        @Valid
        Subcategory subcategory
) {
}
