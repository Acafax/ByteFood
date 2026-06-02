package org.example.posFX.objects;

import org.example.posFX.ItemType;

import java.math.BigDecimal;
import java.util.List;



public class MenuItem {
    private Long id;
    private String name;
    private BigDecimal price;
    private String category;
    private ItemType type;
    private List<MenuItemComboComponent> components;

    public MenuItem(Long id, String name, String category, BigDecimal price, ItemType type, List<MenuItemComboComponent>  components) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.type = type;
        this.components = components;
    }

    public MenuItem() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public void setComponents(List<MenuItemComboComponent>  components) {
        this.components = components;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public ItemType getType() {
        return type;
    }

    public List<MenuItemComboComponent> getComponents() {
        return components;
    }
}
