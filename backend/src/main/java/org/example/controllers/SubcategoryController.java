package org.example.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.dtos.categoryDictionary.CreateSubcategoryDTO;
import org.example.dtos.categoryDictionary.PatchSubcategoryDTO;
import org.example.dtos.categoryDictionary.SubcategoryDTO;
import org.example.services.SubcategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/subcategory")
public class SubcategoryController {


    private final SubcategoryService subCategoriesService;

    public SubcategoryController(SubcategoryService subCategoriesService) {
        this.subCategoriesService = subCategoriesService;
    }

    @GetMapping()
    public ResponseEntity<List<SubcategoryDTO>> getRestaurantSubCategory(){
        List<SubcategoryDTO> subCategoriesFromRestaurant = subCategoriesService.getSubCategoriesFromRestaurant();
        return ResponseEntity.ok(subCategoriesFromRestaurant);
    }

    @PostMapping
    public ResponseEntity<SubcategoryDTO> createSubCategory(@RequestBody CreateSubcategoryDTO createSubCategoryDTO){
        SubcategoryDTO subCategory = subCategoriesService.createSubCategory(createSubCategoryDTO);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(subCategory.id())
                .toUri();

        return ResponseEntity.created(uri).body(subCategory);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SubcategoryDTO> patchSubCategory(
            @PathVariable Long id,
            @Valid @RequestBody PatchSubcategoryDTO patchSubcategoryDTO) {

        SubcategoryDTO updated = subCategoriesService.patchSubCategory(id, patchSubcategoryDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SubcategoryDTO> deleteSubCategory(@PathVariable Long id){
        SubcategoryDTO deleted = subCategoriesService.deleteSubCategory(id);
        return ResponseEntity.ok(deleted);
    }

}
