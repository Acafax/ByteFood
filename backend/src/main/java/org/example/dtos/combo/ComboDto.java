package org.example.dtos.combo;

import org.example.dtos.order.OrderProductDTO;

import java.math.BigDecimal;
import java.util.List;

public record ComboDto(String name, BigDecimal price, List<OrderProductDTO> comboProduct) {
}
