package org.example.dtos.mapers;

import lombok.extern.slf4j.Slf4j;
import org.example.dtos.stockItem.StockItemDTO;
import org.example.models.StockItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class StockItemDTOMapper {

    public StockItemDTO mapToDTO(StockItem stockItem){
        return new StockItemDTO(
                stockItem.getId(),
                stockItem.getPurchasePrice(),
                stockItem.getQuantity(),
                stockItem.getExpirationDate(),
                stockItem.getSemiProduct().getId(),
                stockItem.getRestaurantId()
        );
    }



    /**
        Map < SemiProductId , List of StockItem related to this SemiProduct  >
        This is of faster search in adjustmentStock method
    */
    public Map<Long, List<StockItem>> createMapOfStockItems(List<StockItem> stockItems){
        return stockItems.stream()
                .filter(stockItem -> stockItem.getSemiProductId()!=null)
                .collect(Collectors.groupingBy(StockItem::getSemiProductId));
    }


}
