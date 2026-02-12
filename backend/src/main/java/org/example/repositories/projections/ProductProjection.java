package org.example.repositories.projections;


import java.math.BigDecimal;

public interface ProductProjection {
    Long getId();
    String getName();
    BigDecimal getPrice();
    String getCategory();
}
