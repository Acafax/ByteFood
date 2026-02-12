package org.example.repositories.projections;

import java.math.BigDecimal;

public interface ModificationTemplateProjection {
    Long getId();
    String getName();
    BigDecimal getPrice();
    String getCategory();
    Long getSemiProductId();
    Long getRestaurantId();
}
