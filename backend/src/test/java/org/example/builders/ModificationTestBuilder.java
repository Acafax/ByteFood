package org.example.builders;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;


public class ModificationTestBuilder {

    private Long id =1L;

    private String name = "Modification";

    private BigDecimal price = new BigDecimal("10");

    private String category= "BUGER";

    private Long restaurantId= 1L;


    public ModificationTestBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ModificationTestBuilder withPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public ModificationTestBuilder withCategory(String category) {
        this.category = category;
        return this;
    }

    public ModificationTestBuilder withRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
        return this;
    }

    public Map<String, Object> buildMap() {
        return Map.of(
                "name", name,
                "price", price,
                "category", category,
                "restaurantId", restaurantId
        );
    }
}
