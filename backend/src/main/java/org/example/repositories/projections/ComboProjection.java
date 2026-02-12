package org.example.repositories.projections;

import org.example.models.ComboProduct;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

public interface ComboProjection {
    Long getId();
    String getName();
    BigDecimal getPrice();
    ComboProduct getComboProducts();
}
