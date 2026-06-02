package org.example.dtos.categoryDictionary;

import jakarta.validation.constraints.NotBlank;

public record CreateSubcategoryDTO(

        @NotBlank
        String subcategoryName

) {
}
