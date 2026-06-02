package org.example.posFX.apiCommunication.order;

import java.math.BigDecimal;

public record ModificationRequest(
        String name,
        BigDecimal quantity,
        BigDecimal price,
        Long modificationTemplateId
) {
}
