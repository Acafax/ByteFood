package org.example.services;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.example.dtos.categoryDictionary.CreateSubcategoryDTO;
import org.example.dtos.categoryDictionary.PatchSubcategoryDTO;
import org.example.dtos.categoryDictionary.SubcategoryDTO;
import org.example.dtos.mapers.categoryDictionaryMapper;
import org.example.models.Subcategory;
import org.example.repositories.SubcategoryRepository;
import org.example.security.CustomUserDetailsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubcategoryService {


    private final SubcategoryRepository subCategoriesRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final categoryDictionaryMapper categoryDictionaryMapper;


    public SubcategoryService(SubcategoryRepository subCategoriesRepository, CustomUserDetailsService customUserDetailsService, categoryDictionaryMapper categoryDictionaryMapper) {
        this.subCategoriesRepository = subCategoriesRepository;
        this.customUserDetailsService = customUserDetailsService;
        this.categoryDictionaryMapper = categoryDictionaryMapper;
    }

    @PreAuthorize("@securityService.isEmployee() ")
    public List<SubcategoryDTO> getSubCategoriesFromRestaurant(){
        Long currentRestaurantId = customUserDetailsService.getCurrentRestaurantId();

        List<Subcategory> subCategoriesList = subCategoriesRepository.getSubCategoriesList(currentRestaurantId);

        return categoryDictionaryMapper.mapToListOfDTO(subCategoriesList);
    }

    @PreAuthorize("@securityService.isManager() ")
    public SubcategoryDTO createSubCategory(CreateSubcategoryDTO subCategoryDTO){
        Long currentRestaurantId = customUserDetailsService.getCurrentRestaurantId();
        if (subCategoriesRepository.existsSubcategoryBySubcategoryNameAndRestaurantId(
                subCategoryDTO.subcategoryName(),
                currentRestaurantId)) {
            throw new EntityExistsException(
                    "Subcategory with name '" + subCategoryDTO.subcategoryName() + "' already exists in restaurant"
            );
        }

        Subcategory subCategory = categoryDictionaryMapper.mapToEntity(subCategoryDTO);

        subCategory.setRestaurantId(currentRestaurantId);

        Subcategory createdSubCategory = subCategoriesRepository.save(subCategory);

        return categoryDictionaryMapper.mapToDTO(createdSubCategory);


    }

    @PreAuthorize("@securityService.isManager() ")
    public SubcategoryDTO patchSubCategory(Long id, PatchSubcategoryDTO patchSubcategoryDTO){
        Subcategory subcategory = subCategoriesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subcategory with id " + id + " not found"));

        customUserDetailsService.checkAccessToResource(subcategory.getRestaurantId());
        subcategory.setSubcategoryName(patchSubcategoryDTO.subcategoryName());

        Subcategory savedSubcategory = subCategoriesRepository.save(subcategory);
        return categoryDictionaryMapper.mapToDTO(savedSubcategory);
    }

    @PreAuthorize("@securityService.isManager()")
    public SubcategoryDTO deleteSubCategory(Long id){
        Subcategory subcategory = subCategoriesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subcategory with id " + id + " not found"));

        customUserDetailsService.checkAccessToResource(subcategory.getRestaurantId());
        subCategoriesRepository.delete(subcategory);

        return categoryDictionaryMapper.mapToDTO(subcategory);
    }


}
