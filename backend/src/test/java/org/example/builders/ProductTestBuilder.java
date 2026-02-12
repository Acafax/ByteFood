
package org.example.builders;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

public class ProductTestBuilder {
    private String name = "Test Burger";
    private String category = "BURGER";
    private BigDecimal price = new BigDecimal("20.99");
    private Long restaurantId = 1L;

    public ProductTestBuilder withName(String name){
        this.name=name;
        return this;
    }

    public ProductTestBuilder withCategory(String category){
        this.category=category;
        return this;
    }

    public ProductTestBuilder withPrice(BigDecimal price){
        this.price=price;
        return this;
    }

    public ProductTestBuilder withRestaurantId(Long restaurantId){
        this.restaurantId=restaurantId;
        return this;
    }

    public String buildJson(){
        return """
                name: "%s",
                category: "%s",
                priceOfCombos: %s,
                restaurantId: %d
                """.formatted(name, category, price, restaurantId);
    }

    public Map<String, Object> buildMap(){
        return Map.of(
                "name", name,
                "category", category,
                "priceOfCombos", price,
                "restaurantId", restaurantId);
    }

}
