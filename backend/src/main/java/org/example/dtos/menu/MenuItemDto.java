package org.example.dtos.menu;

import org.example.dtos.combo.ComboProductDTO;

import java.math.BigDecimal;
import java.util.List;

public record MenuItemDto(Long id, String name, String category, BigDecimal price, String type, List<ComboProductDTO> components) {
}
