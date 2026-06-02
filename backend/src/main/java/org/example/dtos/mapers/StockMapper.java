package org.example.dtos.mapers;

import org.example.dtos.semiProducts.SemiProductForStockDTO;
import org.example.dtos.stock.StockItemDTO;
import org.example.dtos.stock.StockItemDictionaryWithoutSemiProductDTO;
import org.example.repositories.projections.StockItemProjection;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class StockMapper {

    public List<StockItemDTO> mapToStockItemDTOList(List<StockItemProjection> stockItemProjections) {
        return stockItemProjections.stream()
                .map(projection -> new StockItemDTO(
                        projection.getId(),
                        projection.getPurchasePrice(),
                        projection.getQuantity(),
                        projection.getExpirationDate(),
                        new SemiProductForStockDTO(
                                projection.getSemiProductId(),
                                projection.getSemiProductName(),
                                projection.getSemiProductUnit(),
                                projection.getSemiProductMinimalStockQuantity(),
                                projection.getRestaurantId()
                        ),
                        projection.getRestaurantId()
                ))
                .toList();
    }


}
