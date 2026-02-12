package org.example.repositories;

import org.example.models.SemiProduct;
import org.example.models.UnitType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface SemiProductRepository extends JpaRepository<SemiProduct, Long> {

    boolean existsSemiProductByNameAndUnitAndRestaurantId(String name, UnitType unit, Long restaurantId);


    @Query("SELECT sp FROM SemiProduct sp where" +
            " sp.restaurantId = :restaurantId ")
    List<SemiProduct> getSemiProducts(@Param("restaurantId") Long restaurantId);

    @Query(" SELECT DISTINCT sp.unit FROM SemiProduct sp " +
            "WHERE sp.restaurantId = :restaurantId ")
    List<UnitType> getSemiProductsUnits(Long restaurantId);

    @Query("SELECT sp FROM SemiProduct sp" +
            " WHERE sp.id IN :ids ")
    Set<SemiProduct> getSemiProductsByIds(@Param("ids") Set<Long> ids);
}
