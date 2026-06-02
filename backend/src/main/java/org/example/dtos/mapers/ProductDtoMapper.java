package org.example.dtos.mapers;

import jakarta.persistence.EntityNotFoundException;
import org.example.dtos.product.CreateProductDto;
import org.example.dtos.product.ProductDto;
import org.example.dtos.product.ProductDtoWithSemiProducts;
import org.example.dtos.product.ProductWithSemiProductIdDto;
import org.example.models.Product;
import org.example.models.ProductSemiProduct;
import org.example.repositories.projections.ProductProjection;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
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
                product.getPrice(),
                product.getRestaurantId()
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
                productProjection.getPrice(),
                productProjection.getRestaurantId()
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
                productProjection.getPrice(),
                productProjection.getRestaurantId()
        );
    }


    public List<ProductWithSemiProductIdDto> mapToProductWithSemiIds(Set<Product> productsWithSemiProductIdsByIds) {
        return productsWithSemiProductIdsByIds.stream()
                .map(product -> new ProductWithSemiProductIdDto(
                        product.getId(),
                        product.getName(),
                        product.getCategory(),
                        product.getPrice(),
                        product.getSemiProducts().stream()
                                .filter(Objects::nonNull)
                                .collect(Collectors.toMap(ProductSemiProduct::getSemiProductId, ProductSemiProduct::getQuantity, BigDecimal::add))
                        )).collect(Collectors.toList());
    }
}























