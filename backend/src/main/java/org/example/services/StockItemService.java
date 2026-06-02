package org.example.services;

import lombok.extern.slf4j.Slf4j;
import org.example.dtos.mapers.OrderDTOMapper;
import org.example.dtos.mapers.StockItemDTOMapper;
import org.example.dtos.order.OrderDto;
import org.example.models.StockItem;
import org.example.repositories.StockItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service responsible for adjusting stock quantities based on incoming orders using FIFO strategy.
 */
@Service
@Slf4j
public class StockItemService {


    private final StockItemRepository stockItemRepository;
    private final OrderDTOMapper orderDTOMapper;
    private final StockItemDTOMapper stockItemDTOMapper;

    public StockItemService(StockItemRepository stockItemRepository, OrderDTOMapper orderDTOMapper, StockItemDTOMapper stockItemDTOMapper) {
        this.stockItemRepository = stockItemRepository;
        this.orderDTOMapper = orderDTOMapper;
        this.stockItemDTOMapper = stockItemDTOMapper;
    }


    /**
     * Adjusts stock item quantities based on the given order using FIFO strategy.
     * Exhausted stock items (quantity = 0) are deleted; remaining ones are updated.
     *
     * @param orderDto order containing semi-product quantities to consume
     * @throws IllegalArgumentException when orderDto is null
     */
    @Transactional
    public void adjustStockItemAmount(OrderDto orderDto){
        Map<Long, BigDecimal> semiProductsFromOrderDto = orderDTOMapper.getSemiProductsFromOrderDto(orderDto);
        Set<Long> semiProductsIds = semiProductsFromOrderDto.keySet();

        List<StockItem> stockItems = stockItemRepository.getStockItemsBySemiProductIds(semiProductsIds);
        log.info("STOCK ITEMS LIST SIZE {} ",stockItems.size());
        log.info("STOCK ITEMS LIST HAS NULL: {}", stockItems.contains(null));
        Map<Long, List<StockItem>> stockItemsBySemiProductId = stockItemDTOMapper.createMapOfStockItems(stockItems);

        logMapOfSemiProducts(stockItemsBySemiProductId);

        List<StockItem> finalAdjustedStockList= new ArrayList<>();
        List<StockItem> stockItemsToDelete = new ArrayList<>();

        semiProductsIds.forEach(key -> {
            log.info("Semi Product ID:{}",key);

            if (stockItemsBySemiProductId.get(key) == null){
                log.warn("Stock Item witch Semi Prodcut id: {} is EMPTY", key);
                return;
            }

            List<StockItem> stockItemList= stockItemsBySemiProductId.get(key);

            List<StockItem> sortedList = stockItemList.stream()
                    .sorted(Comparator.comparing(
                            StockItem::getExpirationDate,
                            Comparator.nullsLast(Comparator.naturalOrder())))
                    .collect(Collectors.toList());


            List<StockItem> adjustedStockItemList = adjustStockItemList(sortedList, semiProductsFromOrderDto, key);
            stockItemsToDelete.addAll(findStockItemsToDelete(adjustedStockItemList));

            finalAdjustedStockList.addAll(adjustedStockItemList);
            finalAdjustedStockList.removeAll(stockItemsToDelete);

        });

        stockItemRepository.deleteAll(stockItemsToDelete);
        stockItemRepository.saveAll(finalAdjustedStockList);
    }

    private void logMapOfSemiProducts(Map<Long, List<StockItem>> stockItemsBySemiProductId) {
        stockItemsBySemiProductId.forEach((key, value) -> log.info("SemiProductID: {}, is NULL: {}  ", key, value==null));

    }

    /**
     Adjust stockItems from map in adjustStockItemAmount method
     Return List with modified StockItems (e.g. [0].quantity = 0, [1].quantity = 100, next and others quantity without changes )
     */
    private List<StockItem> adjustStockItemList(List<StockItem> sortedList,Map<Long, BigDecimal> semiProductsFromOrderDto, Long semiProductId){
        BigDecimal remainingNeededQuantity = semiProductsFromOrderDto.get(semiProductId);

        for (int i=0; i < sortedList.size() ; i ++){
            if (remainingNeededQuantity.compareTo(BigDecimal.ZERO) == 0) break;

            StockItem stockItem = sortedList.get(i);
            // Consume from oldest stock first (FIFO)
            if ( stockItemHaveEnoughQuantity(stockItem, remainingNeededQuantity) ){ // enough quantity of stockItem
                BigDecimal newQuantity = stockItem.getQuantity().subtract(remainingNeededQuantity);
                stockItem.setQuantity(newQuantity);
                remainingNeededQuantity=BigDecimal.ZERO;
            }
            else {
                remainingNeededQuantity= remainingNeededQuantity.subtract(stockItem.getQuantity());
                stockItem.setQuantity(BigDecimal.ZERO);
            }
        }

        return sortedList;
    }

    private boolean stockItemHaveEnoughQuantity(StockItem stockItem, BigDecimal semiProductQuantity){
        return stockItem.getQuantity().compareTo(semiProductQuantity) >= 0;
    }

    private List<StockItem> findStockItemsToDelete(List<StockItem> adjustedStockItemList) {
        return adjustedStockItemList.stream()
                .filter(stockItem -> stockItem.getQuantity().compareTo(BigDecimal.ZERO) == 0) // .compareTo is better then equals()
                .toList();
    }

}
