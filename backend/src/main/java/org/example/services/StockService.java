package org.example.services;

import org.example.dtos.mapers.StockMapper;
import org.example.dtos.stock.StockItemDTO;
import org.example.repositories.StockItemRepository;
import org.example.repositories.projections.StockItemProjection;
import org.example.security.CustomUserDetailsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {

    private final StockItemRepository stockItemRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final StockMapper stockMapper;

    public StockService(StockItemRepository stockItemRepository, CustomUserDetailsService customUserDetailsService, StockMapper stockMapper) {
        this.stockItemRepository = stockItemRepository;
        this.customUserDetailsService = customUserDetailsService;
        this.stockMapper = stockMapper;
    }


    @PreAuthorize("@securityService.isManager()")
    public List<StockItemDTO> getStockData(){
        Long currentRestaurantId = customUserDetailsService.getCurrentRestaurantId();

        List<StockItemProjection> stockItemsByRestaurantId = stockItemRepository.getStockItemsByRestaurantId(currentRestaurantId);

        return stockMapper.mapToStockItemDTOList(stockItemsByRestaurantId);



    }





}
