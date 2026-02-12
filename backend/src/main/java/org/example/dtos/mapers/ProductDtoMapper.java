package org.example.dtos.mapers;

import jakarta.persistence.EntityNotFoundException;
import org.example.dtos.product.CreateProductDto;
import org.example.dtos.product.ProductDto;
import org.example.dtos.product.ProductDtoWithSemiProducts;
import org.example.models.Product;
import org.example.repositories.projections.ProductProjection;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ProductDtoMapper {


    private final SemiProductsDtoMapper semiProductsDtoMapper;

    public ProductDtoMapper(SemiProductsDtoMapper semiProductsDtoMapper) {
        this.semiProductsDtoMapper = semiProductsDtoMapper;
    }

    public ProductDto mapToProductDto(Product product){
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getPrice()
        );

    }

    public Product mapToProduct(CreateProductDto createProductDto, Long currentRestaurantId){
        Product product = new Product();
        product.setName(createProductDto.name());
        product.setCategory(createProductDto.category());
        product.setPrice(createProductDto.price());
        product.setRestaurantId(currentRestaurantId);
        product.setSemiProducts(new HashSet<>());

        return product;
    }

    public ProductDtoWithSemiProducts mapToProductDtoWithSemiProduct(Product product){
        return new ProductDtoWithSemiProducts(
                product.getName(),
                product.getCategory(),
                product.getPrice(),
                product.getSemiProducts().stream()
                        .map(semiProduct -> semiProductsDtoMapper.mapToSemiProductDto(semiProduct.getSemiProduct()))
                        .collect(Collectors.toSet()));
    }

    public ProductDto mapToProductDTO(ProductProjection productProjection) {
        if (productProjection == null) {
            return null;
        }
        return new ProductDto(
                productProjection.getId(),
                productProjection.getName(),
                productProjection.getCategory(),
                productProjection.getPrice()
        );
    }

    public Product mapToProduct(ProductProjection productProjection) {
        if (productProjection == null) {
            return null;
        }
        return new Product(
                productProjection.getId(),
                productProjection.getName(),
                productProjection.getCategory(),
                productProjection.getPrice()
        );
    }





}
