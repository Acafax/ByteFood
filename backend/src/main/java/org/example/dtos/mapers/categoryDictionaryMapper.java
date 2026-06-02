package org.example.dtos.mapers;

import org.example.dtos.categoryDictionary.CreateSubcategoryDTO;
import org.example.dtos.categoryDictionary.SubcategoryDTO;
import org.example.models.Subcategory;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class categoryDictionaryMapper {

    public List<SubcategoryDTO> mapToListOfDTO(List<Subcategory> subCategories){
       return subCategories.stream().map(subCategory -> {
            return new SubcategoryDTO(
                    subCategory.getId(),
                    subCategory.getSubcategoryName(),
                    subCategory.getRestaurantId()
            );
        }).toList();
    }


    public Subcategory mapToEntity(CreateSubcategoryDTO subCategory) {
        return new Subcategory(
                subCategory.subcategoryName()
        );
    }

    public SubcategoryDTO mapToDTO(Subcategory createdSubCategory) {
        return new SubcategoryDTO(
                createdSubCategory.getId(),
                createdSubCategory.getSubcategoryName(),
                createdSubCategory.getRestaurantId()
        );
    }
}
