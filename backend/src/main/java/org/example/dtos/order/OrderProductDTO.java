package org.example.dtos.order;

import org.example.dtos.product.ProductDto;

import java.math.BigDecimal;
import java.util.List;

public record OrderProductDTO(Integer quantity, BigDecimal price, String description, ProductDto product, List<OrderItemModificationDTO> modifications) {
}