package org.example.services;

import org.example.repositories.ProductSemiProductRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductSemiProductService {


    private final ProductSemiProductRepository productSemiProductRepository;

    public ProductSemiProductService(ProductSemiProductRepository productSemiProductRepository) {
        this.productSemiProductRepository = productSemiProductRepository;
    }




}
