package org.example.dtos.combo;

import java.math.BigDecimal;
import java.util.List;

public record ComboDetailsDTO(String name, BigDecimal price, List<ComboProductDTO> comboProduct) {
}
