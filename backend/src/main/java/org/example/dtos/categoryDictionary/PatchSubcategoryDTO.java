package org.example.dtos.categoryDictionary;

import jakarta.validation.constraints.NotBlank;

public record PatchSubcategoryDTO(
        @NotBlank
        String subcategoryName
) {
}

