package org.example.posFX.apiCommunication.order;

import java.math.BigDecimal;
import java.util.List;

public record OrderProductRequest(
        Integer quantity,
        BigDecimal price,
        String description,
        Long productId,
        List<ModificationRequest> modifications
) {
}
