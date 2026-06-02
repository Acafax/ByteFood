package org.example.controllers;

import org.example.dtos.stock.StockItemDTO;
import org.example.services.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
public class StockController {


    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping()
    public ResponseEntity<List<StockItemDTO>> restaurantStock() {
        List<StockItemDTO> stockData = stockService.getStockData();
        return ResponseEntity.ok(stockData);
    }



}
