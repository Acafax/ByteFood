package org.example.dtos.combo;

import org.example.dtos.order.OrderProductDTO;
import org.example.dtos.order.OrderProductRequestDTO;

import java.math.BigDecimal;
import java.util.List;

public record ComboRequestDTO(String name, Integer quantity , BigDecimal price, Long comboId ,
                              List<OrderProductRequestDTO> comboProducts) {
}
