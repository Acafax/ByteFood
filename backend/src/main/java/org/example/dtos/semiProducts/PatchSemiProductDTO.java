package org.example.dtos.semiProducts;

import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMin;
import org.example.models.UnitType;
import org.example.util.AtLeastOneNotNull;
import org.example.util.NullOrValid;

import java.math.BigDecimal;

@AtLeastOneNotNull(message = "At least one field must be provided for patch operation")
public record PatchSemiProductDTO(

    @NullOrValid
    String name,

    @DecimalMin(value = "0.01", message = "carbohydrate have to be higher then 0")
    BigDecimal carbohydrate,

    @DecimalMin(value = "0.01", message = "fat have to be higher then 0")
    BigDecimal fat,

    @DecimalMin(value = "0.01", message = "protein have to be higher then 0")
    BigDecimal protein,

    UnitType unit

){}
