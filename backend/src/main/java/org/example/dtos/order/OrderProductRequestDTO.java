package org.example.dtos.order;

import org.example.dtos.ModificationRequestDTO;

import java.math.BigDecimal;
import java.util.List;

public record OrderProductRequestDTO(Integer quantity, BigDecimal price, String description, Long productId ,
                                     List<ModificationRequestDTO> modifications) {
}
