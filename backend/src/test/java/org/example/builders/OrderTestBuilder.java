package org.example.builders;

import org.example.models.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class OrderTestBuilder {

    private Set<OrderProduct> orderItems = Set.of();
    private Set<OrderCombo> combos = Set.of();


    public static OrderTestBuilder order(){
        return new OrderTestBuilder();
    }

    public OrderTestBuilder withProduct(Long productId){
        Product product = new Product();
        product.setId(productId);

        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setProduct(product);

        orderProduct.setModifications(List.of());
        this.orderItems = new HashSet<>(orderItems);
        this.orderItems.add(orderProduct);
        return this;
    }

    public OrderTestBuilder withProductWithModification(Long productId, Long modificationId){
        Product product = new Product();
        product.setId(productId);

        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setProduct(product);

        ModificationTemplate modificationTemplate = new ModificationTemplate();
        modificationTemplate.setId(3L);

        OrderItemModification orderItemModification = new OrderItemModification();
        orderItemModification.setModificationTemplate(modificationTemplate);

        orderProduct.setModifications(List.of(orderItemModification));
        this.orderItems = new HashSet<>(orderItems);
        this.orderItems.add(orderProduct);
        return this;
    }




    public OrderTestBuilder withCombo(Long comboId, Long... productIds){
        OrderCombo combo = new OrderCombo();
        combo.setId(comboId);
        List<OrderProduct> comboProducts = Arrays.stream(productIds)
                .map(id -> {
                    Product product = new Product();
                    product.setId(id);
                    OrderProduct orderProduct = new OrderProduct();
                    orderProduct.setProduct(product);
                    return orderProduct;
                }).toList();

        combo.setProductsInCombo(comboProducts);
        this.combos = new HashSet<>(combos);
        this.combos.add(combo);
        return this;
    }

    public OrderTestBuilder withComboWithModificationInFirstProduct(Long comboId, Long modificationTemplateId,Long... productIds){
        OrderCombo combo = new OrderCombo();
        combo.setId(comboId);
        List<OrderProduct> comboProducts = Arrays.stream(productIds)
                .map(id -> {
                    Product product = new Product();
                    product.setId(id);

                    OrderProduct orderProduct = new OrderProduct();
                    orderProduct.setProduct(product);

                    OrderItemModification orderItemModification = new OrderItemModification();
                    ModificationTemplate modificationTemplate = new ModificationTemplate();
                    modificationTemplate.setId(modificationTemplateId);
                    orderItemModification.setModificationTemplate(modificationTemplate);
                    orderProduct.setModifications(List.of(orderItemModification));

                    return orderProduct;
                }).toList();

        combo.setProductsInCombo(comboProducts);
        this.combos = new HashSet<>(combos);
        this.combos.add(combo);
        return this;
    }

    public Order build(){
        Order order = new Order();
        order.setOrderItems(orderItems);
        order.setCombos(combos);
        return order;
    }

}
