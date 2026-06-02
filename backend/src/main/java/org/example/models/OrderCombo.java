package org.example.models;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Table(
        name = "order_combo",
        indexes = {
                @Index(name = "idx_order_combo_restaurant_id", columnList = "restaurant_id")
        }
)
@Entity
@Getter
@Setter
@NoArgsConstructor
public class OrderCombo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;

    private BigDecimal comboPrice;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "combo_id")
    private Combo combo;

    @OneToMany(mappedBy = "partOfCombo", cascade = CascadeType.ALL)
    private List<OrderProduct> productsInCombo = new ArrayList<>();

    @Column(name = "restaurant_id")
    private Long restaurantId;

    public OrderCombo(Integer quantity, BigDecimal comboPrice, Order order, Combo combo, List<OrderProduct> productsInCombo, Long restaurantId) {
    }
}
