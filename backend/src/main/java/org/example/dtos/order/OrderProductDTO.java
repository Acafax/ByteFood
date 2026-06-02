package org.example.dtos.order;

import org.example.dtos.product.ProductDto;
import org.example.dtos.product.ProductWithSemiProductIdDto;

import java.math.BigDecimal;
import java.util.List;

public record OrderProductDTO(Integer quantity, BigDecimal price, String description, ProductWithSemiProductIdDto product, List<OrderItemModificationDTO> modifications) {


}