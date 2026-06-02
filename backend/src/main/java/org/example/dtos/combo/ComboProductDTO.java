package org.example.dtos.combo;

import org.example.dtos.product.ProductDto;

public record ComboProductDTO(Integer quantity, ProductDto productDto) {
}
