package org.example.controllers;


import org.example.models.UnitType;
import org.example.services.ProductService;
import org.example.services.SemiProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MetadataController {


    private final ProductService productService;
    private final SemiProductService semiProductService;

    public MetadataController(ProductService productService, SemiProductService semiProductService) {
        this.productService = productService;
        this.semiProductService = semiProductService;
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories(){
        List<String> productCategories = productService.getProductCategories();

        return ResponseEntity.ok().body(productCategories);
    }

    @GetMapping("/units")
    public ResponseEntity<List<UnitType>> getSemiProductsUnits(){
        List<UnitType> productCategories = semiProductService.getSemiProductsUnits();

        return ResponseEntity.ok().body(productCategories);
    }


}
