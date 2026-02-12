package org.example.dtos.order;

import org.example.dtos.combo.ComboProductDTO;

import java.math.BigDecimal;
import java.util.List;

public record OrderComboDTO(Integer quantity, BigDecimal comboPrice, Long comboId, List<ComboProductDTO> products) {
}
