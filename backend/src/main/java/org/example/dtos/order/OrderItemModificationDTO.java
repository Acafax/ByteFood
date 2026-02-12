package org.example.dtos.order;

import org.example.dtos.modification.ModificationTemplateDto;

import java.math.BigDecimal;

public record OrderItemModificationDTO(Integer quantity, BigDecimal price,  ModificationTemplateDto modificationTemplateDto) {
}
