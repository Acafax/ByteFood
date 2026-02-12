package org.example.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Table(name = "orders",
    indexes ={
        @Index(name = "idx_order_restaurant_id", columnList = "restaurant_id"),
            @Index(name = "idx_order_restaurant_id_time", columnList = "restaurant_id,orderTime")
})
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime orderTime;

    private Duration preparationTime;

    private BigDecimal price;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderProduct> orderItems;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderCombo> combos;

    @Column(name = "restaurant_id")
    private Long restaurantId;

    public void addOrderCombo(OrderCombo orderCombo, Integer quantity, BigDecimal comboPrice, Combo combo, List<OrderProduct> productsInCombo, Long restaurantId){
        OrderCombo newOrderCombo = new OrderCombo(
                quantity,
                comboPrice,
                this,
                combo,
                productsInCombo,
                restaurantId
        );

        this.combos.add(orderCombo);
    }

    public void addOrderProduct(OrderProduct orderProduct, BigDecimal price, Integer quantity, String description, Product product, OrderCombo partOfCombo ,List<OrderItemModification> modifications, Long restaurantId){
        OrderProduct newOrderProduct = new OrderProduct(
                price,
                quantity,
                description,
                this,
                product,
                partOfCombo,
                modifications,
                restaurantId
        );
        this.orderItems.add(orderProduct);
    }


}
