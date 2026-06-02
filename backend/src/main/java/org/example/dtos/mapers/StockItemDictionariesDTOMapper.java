package org.example.dtos.mapers;

import org.example.dtos.semiProducts.SemiProductForStockDTO;
import org.example.dtos.stockItemDictionary.StockItemDictionaryDTO;
import org.example.dtos.stockItemDictionary.StockItemDictionaryWithoutSemiProductDTO;
import org.example.models.SemiProduct;
import org.example.models.StockItemDictionary;
import org.example.repositories.projections.StockItemDictionaryProjection;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class StockItemDictionariesDTOMapper {

    private final SemiProductsDtoMapper semiProductsDtoMapper;

    public StockItemDictionariesDTOMapper(SemiProductsDtoMapper semiProductsDtoMapper) {
        this.semiProductsDtoMapper = semiProductsDtoMapper;
    }

    public StockItemDictionaryDTO mapToStockItemDictionary(StockItemDictionaryProjection projection){
        return new StockItemDictionaryDTO(
                projection.getId(),
                projection.getName(),
                projection.getPrice(),
                projection.getUnit(),
                projection.getRestaurantId(),
                projection.getMultipleOfSemiProduct(),
                semiProductsDtoMapper.maptoSemiProductForStockDTO(projection.getSemiProduct())
        );
    }
    public List<StockItemDictionaryDTO> mapToStockItemDictionary(List<StockItemDictionaryProjection> projections){
        return projections.stream()
                .map(this::mapToStockItemDictionary)
                .toList();
    }

    public StockItemDictionaryWithoutSemiProductDTO mapToWithoutSemiProductDTO(StockItemDictionary entity) {
        return new StockItemDictionaryWithoutSemiProductDTO(
                entity.getId(),
                entity.getName(),
                entity.getPrice(),
                entity.getUnit(),
                entity.getRestaurantId(),
                entity.getMultipleOfSemiProduct()
        );
    }

    public StockItemDictionaryDTO mapToDTO(StockItemDictionary stockItemDictionary) {
        SemiProduct semiProduct = stockItemDictionary.getSemiProduct();
        SemiProductForStockDTO semiProductForStockDTO = null;
        if (semiProduct != null) {
            semiProductForStockDTO = new SemiProductForStockDTO(
                    semiProduct.getId(),
                    semiProduct.getName(),
                    semiProduct.getUnit(),
                    semiProduct.getMinimalStockQuantity(),
                    semiProduct.getRestaurantId()
            );
        }
        return new StockItemDictionaryDTO(
                stockItemDictionary.getId(),
                stockItemDictionary.getName(),
                stockItemDictionary.getPrice(),
                stockItemDictionary.getUnit(),
                stockItemDictionary.getRestaurantId(),
                stockItemDictionary.getMultipleOfSemiProduct(),
                semiProductForStockDTO
        );
    }
}
