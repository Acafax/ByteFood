package org.example.dtos.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.example.dtos.modification.ModificationTemplateDto;

import java.math.BigDecimal;

public record OrderItemModificationDTO(BigDecimal quantity, BigDecimal price,  ModificationTemplateDto modificationTemplateDto) {

    @JsonIgnore
    public Long getSemiProductId(){
        return modificationTemplateDto().semiProductId();
    }

}
