package org.example.builders;

import org.example.dtos.product.ProductWithSemiProductIdDto;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ProductWithSemiProductIdDtoTestBuilder {

    private Long id = 1L;
    private String name = "Product";
    private String category = null;
    private BigDecimal price = null;
    private Map<Long, BigDecimal> productSemiProductIdQuantity = null;

    public static ProductWithSemiProductIdDtoTestBuilder productWithSemiIds() {
        return new ProductWithSemiProductIdDtoTestBuilder();
    }

    public static List<ProductWithSemiProductIdDto> forIds(Long... ids) {
        return Arrays.stream(ids)
                .map(id -> productWithSemiIds().withId(id).withName("Product " + id).build())
                .toList();
    }

    public ProductWithSemiProductIdDtoTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public ProductWithSemiProductIdDtoTestBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ProductWithSemiProductIdDtoTestBuilder withCategory(String category) {
        this.category = category;
        return this;
    }

    public ProductWithSemiProductIdDtoTestBuilder withPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public ProductWithSemiProductIdDtoTestBuilder withProductSemiProductIdQuantity(Map<Long, BigDecimal> productSemiProductIdQuantity) {
        this.productSemiProductIdQuantity = productSemiProductIdQuantity;
        return this;
    }

    public ProductWithSemiProductIdDto build() {
        return new ProductWithSemiProductIdDto(id, name, category, price, productSemiProductIdQuantity);
    }
}
