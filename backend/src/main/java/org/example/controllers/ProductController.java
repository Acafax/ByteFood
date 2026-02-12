package org.example.controllers;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.dtos.product.CreateProductDto;
import org.example.dtos.product.PatchProductDTO;
import org.example.dtos.product.ProductDto;
import org.example.dtos.product.ProductDtoWithSemiProducts;
import org.example.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/products")
public class ProductController {


    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}/with-semi")
    ResponseEntity<ProductDtoWithSemiProducts> getProductWithSemiProductsById(@PathVariable Long id){
        ProductDtoWithSemiProducts productById = productService.getProductWithSemiProductsById(id);
        return ResponseEntity.ok().body(productById);
    }

    @GetMapping("/all-with-semi")
    ResponseEntity<List<ProductDtoWithSemiProducts>> getProductsOfCurrentRestaurantWithSemiProducts(){
        List<ProductDtoWithSemiProducts> allRestaurantProducts = productService.getAllRestaurantProducts();
        return ResponseEntity.ok().body(allRestaurantProducts);
    }

    @GetMapping("/{id}")
    ResponseEntity<ProductDto> getProductById(@PathVariable Long id){
        ProductDto productById = productService.getProductDtoById(id);
        return ResponseEntity.ok().body(productById);
    }

    @PostMapping()
    ResponseEntity<ProductDto> createProduct( @Valid @RequestBody CreateProductDto createProductDto){
        ProductDto createdProduct = productService.createProduct(createProductDto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{Id}")
                .buildAndExpand(createdProduct.id())
                .toUri();

        return ResponseEntity.created(uri).body(createdProduct);
    }

    @PatchMapping("/{id}")
    ResponseEntity<ProductDto> patchProduct(@PathVariable Long id, @Valid @RequestBody PatchProductDTO patchProductData){
        ProductDto productDto = productService.patchProduct(id, patchProductData);
        return ResponseEntity.ok().body(productDto);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ProductDto> deleteProduct(@PathVariable Long id){
        ProductDto deletedProduct = productService.deleteProduct(id);
        return ResponseEntity.ok().body(deletedProduct);
    }





}
