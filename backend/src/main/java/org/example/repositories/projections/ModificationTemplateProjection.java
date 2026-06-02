package org.example.repositories.projections;

import org.example.models.Subcategory;

import java.math.BigDecimal;

public interface ModificationTemplateProjection {
    Long getId();
    String getName();
    BigDecimal getPrice();
    Subcategory getSubcategory();
    Long getSemiProductId();
    Long getRestaurantId();
}
