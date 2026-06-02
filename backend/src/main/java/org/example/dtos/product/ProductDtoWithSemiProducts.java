package org.example.dtos.product;


import org.example.dtos.semiProducts.SemiProductDTO;

import java.math.BigDecimal;
import java.util.Set;

public record ProductDtoWithSemiProducts(String name, String category, BigDecimal price, Set<SemiProductDTO> semiProducts) { }
