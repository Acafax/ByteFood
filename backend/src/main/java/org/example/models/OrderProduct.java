package org.example.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "order_product",
        indexes = {
                @Index(name = "idx_order_product_restaurant_id", columnList = "restaurant_id")
        })
public class OrderProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal price;

    private Integer quantity;

    private String description;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_combo_id")
    private OrderCombo partOfCombo;

    @OneToMany(mappedBy = "orderProduct", cascade = CascadeType.ALL, orphanRemoval = true)
    List<OrderItemModification> modifications;

    @Column(name = "restaurant_id")
    private Long restaurantId;

    public OrderProduct(Long id, BigDecimal price, Integer quantity, String description, List<OrderItemModification> modifications) {
        this.id = id;
        this.price = price;
        this.quantity = quantity;
        this.description = description;
        this.modifications = modifications;
    }

    public OrderProduct(BigDecimal price, Integer quantity, String description, Order order, Product product, OrderCombo partOfCombo, List<OrderItemModification> modifications, Long restaurantId) {
    }
}
