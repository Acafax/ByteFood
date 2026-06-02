package org.example.posFX.objects;


import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Objects;

import static org.example.posFX.ProductController.possibleModificationMap;

public class OrderItem {

    private MenuItem menuItem;
    private int quantity;
    private LinkedHashMap<String, Integer> modify;

    public OrderItem(MenuItem menuItem, int quantity) { this.menuItem = menuItem; this.quantity = quantity;
        this.menuItem = menuItem;
        this.quantity= quantity;
    }

    public OrderItem(MenuItem menuItem, int quantity, LinkedHashMap<String, Integer> modify) {
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.modify = modify;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setModify(LinkedHashMap<String, Integer> modify){
        this.modify = modify;
    }

    public  LinkedHashMap<String,Integer> getModify() {
        return modify;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public BigDecimal priceOfItemWithMods(OrderItem orderItem){
        BigDecimal onlyItemPrice = orderItem.getMenuItem().getPrice().multiply(new BigDecimal(orderItem.getQuantity()));
        BigDecimal priceOfModyfications = BigDecimal.ZERO;
        if (orderItem.modify != null){
            priceOfModyfications = orderItem.getModify().entrySet().stream()
                    .map(item -> possibleModificationMap.get(item.getKey()).price().multiply(new BigDecimal(item.getValue())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        return onlyItemPrice.add(priceOfModyfications);
    }
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return quantity == orderItem.quantity && Objects.equals(menuItem, orderItem.menuItem) && Objects.equals(modify, orderItem.modify);
    }

    @Override
    public int hashCode() {
        return Objects.hash(menuItem, quantity, modify);
    }
}
