package org.example.builders;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

public class SemiProductTestBuilder {

    private String name = "Test SemiProduct";
    private BigDecimal carbohydrate = new BigDecimal("10.0");
    private BigDecimal fat = new BigDecimal("5.0");
    private BigDecimal protein = new BigDecimal("7.0");
    private String unit = "G";

    public SemiProductTestBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public SemiProductTestBuilder withCarbohydrate(BigDecimal carbohydrate) {
        this.carbohydrate = carbohydrate;
        return this;
    }

    public SemiProductTestBuilder withFat(BigDecimal fat) {
        this.fat = fat;
        return this;
    }

    public SemiProductTestBuilder withProtein(BigDecimal protein) {
        this.protein = protein;
        return this;
    }

    public SemiProductTestBuilder withUnit(String unit) {
        this.unit = unit;
        return this;
    }



    public Map<String, Object> buildMap() {
        return Map.of(
                "name", name,
                "carbohydrate", carbohydrate,
                "fat", fat,
                "protein", protein,
                "unit", unit
        );
    }
}