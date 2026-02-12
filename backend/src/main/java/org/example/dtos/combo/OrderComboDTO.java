package org.example.dtos.combo;

import java.math.BigDecimal;

public record OrderComboDTO(Integer quantity , BigDecimal priceOfCombos, ComboDto combo) {

}
