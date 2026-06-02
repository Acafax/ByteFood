package org.example.dtos.stockItemDictionary;

import org.example.models.UnitType;
import org.example.util.annotation.AtLeastOneNotNull;
import org.example.util.annotation.NullOrValid;

import java.math.BigDecimal;

@AtLeastOneNotNull(message = "At least one field must be provided for patch operation")
public record PatchStockItemDictionaryDTO(

        @NullOrValid
        String name,

        @NullOrValid
        BigDecimal price,

        UnitType unit,

        @NullOrValid
        BigDecimal multipleOfSemiProduct,

        Long semiProductID
) {
}

