package org.example.util.utilityClass;

import lombok.experimental.UtilityClass;
import org.example.models.OrderCombo;
import org.example.models.OrderItemModification;
import org.example.models.OrderProduct;

import java.util.stream.Stream;

@UtilityClass
public class OrderHelper {

    public Long getProductId(OrderProduct orderProduct){
        return orderProduct.getProduct().getId();
    }

    public Stream<Long> getProductsInCombo(OrderCombo combo){
        return combo.getProductsInCombo().stream()
                .map(OrderHelper::getProductId);
    }

    private Long getOrderItemModificationId(OrderItemModification orderItemModification){
        return orderItemModification.getModificationTemplate().getId();
    }

    public Stream<Long> getModificationTemplateIds(OrderProduct orderProduct){
        return orderProduct.getModifications().stream()
                .map(OrderHelper::getOrderItemModificationId);
    }





}
