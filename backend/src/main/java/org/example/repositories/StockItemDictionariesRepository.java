package org.example.repositories;

import org.example.models.StockItemDictionary;
import org.example.repositories.projections.StockItemDictionaryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface StockItemDictionariesRepository extends JpaRepository<StockItemDictionary,Long> {


    @Query("""
        SELECT sid
        FROM StockItemDictionary sid
            LEFT JOIN FETCH sid.semiProduct sp
        WHERE sid.restaurantId = :restaurantId
        """)
    List<StockItemDictionaryProjection> getStockItemDictionariesByRestaurantId(@Param("restaurantId") Long restaurantId);


}
