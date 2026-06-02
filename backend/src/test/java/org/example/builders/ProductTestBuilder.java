
package org.example.builders;

import org.example.dtos.CreateProductSemiProductsDto;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProductTestBuilder {
    private String name = "Test Burger";
    private String category = "BURGER";
    private BigDecimal price = new BigDecimal("20.99");
    private Set<CreateProductSemiProductsDto> productsSemiProducts = new HashSet<>(Set.of(new CreateProductSemiProductsDto(1L, BigDecimal.ONE)));
    private Long restaurantId = 1L;

    public ProductTestBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ProductTestBuilder withCategory(String category) {
        this.category = category;
        return this;
    }

    public ProductTestBuilder withPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public ProductTestBuilder withRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
        return this;
    }

    public ProductTestBuilder withSemiProducts(Set<CreateProductSemiProductsDto> semiProducts) {
        this.productsSemiProducts = new HashSet<>(semiProducts);
        return this;
    }

    public String buildJson() {
        return """
                name: "%s",
                category: "%s",
                priceOfCombos: %s,
                productsSemiProducts: %s,
                restaurantId: %d
                """.formatted(name, category, price, productsSemiProducts, restaurantId);
    }

    public Map<String, Object> buildMap() {
        return Map.of(
                "name", name,
                "category", category,
                "priceOfCombos", price,
                "productsSemiProducts", productsSemiProducts,
                "restaurantId", restaurantId);
    }

}
