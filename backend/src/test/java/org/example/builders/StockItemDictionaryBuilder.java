package org.example.builders;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


public class StockItemDictionaryBuilder {
    private Long id = 1L;
    private String name = "carton of lettuce";
    private BigDecimal price = new BigDecimal("100");
    private String unit = "G";
    private Long restaurantId = 1L;
    private BigDecimal multipleOfSemiProduct = new BigDecimal("7000");
    private Long semiProductID = 14L;

    public StockItemDictionaryBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public StockItemDictionaryBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public StockItemDictionaryBuilder withPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public StockItemDictionaryBuilder withUnit(String unit) {
        this.unit = unit;
        return this;
    }

    public StockItemDictionaryBuilder withRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
        return this;
    }

    public StockItemDictionaryBuilder withMultipleOfSemiProduct(BigDecimal multipleOfSemiProduct) {
        this.multipleOfSemiProduct = multipleOfSemiProduct;
        return this;
    }

    public StockItemDictionaryBuilder withSemiProductID(Long semiProductID) {
        this.semiProductID = semiProductID;
        return this;
    }

    public Map<String, Object> buildMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("price", price);
        map.put("unit", unit);
        map.put("multipleOfSemiProduct", multipleOfSemiProduct);
        map.put("semiProductID", semiProductID);
        return map;
    }
}
