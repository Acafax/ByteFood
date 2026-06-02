package org.example.repositories;

import org.example.models.StockItem;
import org.example.repositories.projections.StockItemProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface StockItemRepository extends JpaRepository<StockItem, Long> {


    @Query("""
            SELECT 
                si.id as id, 
                si.purchasePrice as purchasePrice, 
                si.quantity as quantity, 
                si.expirationDate as expirationDate,
                si.restaurantId as restaurantId,
                
                sp.id as semiProductId,
                sp.name as semiProductName,
                sp.unit as semiProductUnit,
                sp.minimalStockQuantity as semiProductMinimalStockQuantity
            FROM StockItem si 
            LEFT JOIN si.semiProduct sp
            WHERE si.restaurantId = :restaurantId """)
    List<StockItemProjection> getStockItemsByRestaurantId(@Param("restaurantId") Long restaurantId);


    @Query("""
        SELECT si FROM StockItem si
        LEFT JOIN FETCH  si.semiProduct sp
        WHERE si.semiProduct.id IN :semiProductsIds
    """)
    List<StockItem> getStockItemsBySemiProductIds(@Param("semiProductsIds") Set<Long> semiProductsIds);

}
