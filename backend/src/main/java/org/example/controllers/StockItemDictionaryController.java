package org.example.controllers;

import jakarta.validation.Valid;
import org.example.dtos.stockItemDictionary.CreateStockItemDictionaryDTO;
import org.example.dtos.stockItemDictionary.PatchStockItemDictionaryDTO;
import org.example.dtos.stockItemDictionary.StockItemDictionaryDTO;
import org.example.dtos.stockItemDictionary.StockItemDictionaryWithoutSemiProductDTO;
import org.example.services.StockItemDictionariesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/stock-item-dictionaries")
public class StockItemDictionaryController {

    private final StockItemDictionariesService stockItemDictionariesService;

    public StockItemDictionaryController(StockItemDictionariesService stockItemDictionariesService) {
        this.stockItemDictionariesService = stockItemDictionariesService;
    }

    @GetMapping()
    public ResponseEntity<List<StockItemDictionaryDTO>> getRestaurantStockItemDictionaries() {
        List<StockItemDictionaryDTO> stockItemDictionaries = stockItemDictionariesService.getRestaurantStockItemDictionariesDTO();
        return ResponseEntity.ok().body(stockItemDictionaries);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockItemDictionaryWithoutSemiProductDTO> getStockItemDictionaryById(@PathVariable Long id) {
        StockItemDictionaryWithoutSemiProductDTO stockItemDictionary = stockItemDictionariesService.getStockItemDictionaryDTO(id);
        return ResponseEntity.ok().body(stockItemDictionary);
    }

    @PostMapping()
    public ResponseEntity<StockItemDictionaryWithoutSemiProductDTO> createStockItemDictionary(
            @RequestBody @Valid CreateStockItemDictionaryDTO createDTO) {

        StockItemDictionaryWithoutSemiProductDTO created = stockItemDictionariesService.createStockItemDictionary(createDTO);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{Id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(uri).body(created);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<StockItemDictionaryWithoutSemiProductDTO> patchStockItemDictionary(
            @PathVariable Long id,
            @Valid @RequestBody PatchStockItemDictionaryDTO patchDTO) {

        StockItemDictionaryWithoutSemiProductDTO patched = stockItemDictionariesService.patchStockItemDictionary(id, patchDTO);
        return ResponseEntity.ok().body(patched);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StockItemDictionaryWithoutSemiProductDTO> deleteStockItemDictionary(@PathVariable Long id) {
        StockItemDictionaryWithoutSemiProductDTO deleted = stockItemDictionariesService.deleteStockItemDictionary(id);
        return ResponseEntity.ok().body(deleted);
    }
}

