package org.example.dtos;

import java.math.BigDecimal;

public record ModificationRequestDTO(String name,Integer quantity, BigDecimal price, Long modificationTemplateId) {
}
