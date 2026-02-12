package org.example.dtos.product;

import org.example.models.Product;

import java.math.BigDecimal;

public record ProductDto(Long id, String name, String category, BigDecimal price) {
}
