package org.example.repositories;

import org.example.models.StockItem;
import org.example.repositories.projections.StockItemProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StockItemRepository extends JpaRepository<StockItem, Long> {


    @Query("""
            SELECT 
                si.id as id, 
                si.purchasePrice as purchasePrice, 
                si.quantity as quantity, 
                si.expirationDate as expirationDate,
                si.restaurantId as restaurantId,
                
                sid.id as dictionaryId,
                sid.name as dictionaryName,
                sid.unit as dictionaryUnit,
                sid.price as dictionaryPrice,
                sid.minimalStockQuantity as dictionaryMinimalStockQuantity
            FROM StockItem si 
            LEFT JOIN si.stockItemDictionary sid
            WHERE si.restaurantId = :restaurantId """)
    List<StockItemProjection> getStockItemsByRestaurantId(@Param("restaurantId") Long restaurantId);


}
