package org.example.builders;

import java.math.BigDecimal;
import java.util.Map;


public class ModificationTestBuilder {

    private String name = "Modification";

    private BigDecimal price = new BigDecimal("10");

    private Long subcategoryId = 1L;

    public ModificationTestBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ModificationTestBuilder withPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public ModificationTestBuilder withSubcategoryId(Long subcategoryId) {
        this.subcategoryId = subcategoryId;
        return this;
    }

    public Map<String, Object> buildMap() {
        return Map.of(
                "name", name,
                "price", price,
                "subcategory", Map.of("id", subcategoryId)
        );
    }
}
