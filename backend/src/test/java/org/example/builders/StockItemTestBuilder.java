package org.example.builders;

import org.example.models.SemiProduct;
import org.example.models.StockItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class StockItemTestBuilder {

    private Long id = 1L;

    private BigDecimal purchasePrice = new BigDecimal("100");

    private BigDecimal quantity = new BigDecimal("1.00");

    private LocalDateTime expirationDate = LocalDateTime.now().plusMonths(6);

    private Long semiProductId = 1L;

    private Long restaurantId = 1L;

    public StockItemTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public StockItemTestBuilder withPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
        return this;
    }

    public StockItemTestBuilder withQuantity(BigDecimal quantity) {
        this.quantity = quantity;
        return this;
    }

    public StockItemTestBuilder withExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
        return this;
    }

    public StockItemTestBuilder withSemiProductId(Long semiProductId) {
        this.semiProductId = semiProductId;
        return this;
    }

    public StockItemTestBuilder withRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
        return this;
    }

    public StockItem build() {
        SemiProduct semiProduct = new SemiProduct();
        semiProduct.setId(semiProductId);

        StockItem stockItem = new StockItem();
        stockItem.setId(id);
        stockItem.setPurchasePrice(purchasePrice);
        stockItem.setQuantity(quantity);
        stockItem.setExpirationDate(expirationDate);
        stockItem.setSemiProduct(semiProduct);
        stockItem.setRestaurantId(restaurantId);

        return stockItem;
    }

    public Map<String, Object> buildMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("purchasePrice", purchasePrice);
        map.put("quantity", quantity);
        map.put("expirationDate", expirationDate);
        map.put("semiProductId", semiProductId);
        map.put("restaurantId", restaurantId);
        return map;
    }

}
