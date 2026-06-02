package org.example.repositories;

import org.example.models.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubcategoryRepository extends JpaRepository<Subcategory, Long> {

    boolean existsSubcategoryBySubcategoryNameAndRestaurantId(String subcategoryName, Long restaurantId);

    @Query("SELECT sc FROM Subcategory sc WHERE sc.restaurantId = :restaurantId")
    List<Subcategory> getSubCategoriesList (@Param("restaurantId") Long restaurantId);



}
