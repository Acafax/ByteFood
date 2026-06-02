package org.example.dtos.categoryDictionary;

public record SubcategoryDTO(
        Long id,
        String subcategory_name,
        Long restaurantId
) {
}
