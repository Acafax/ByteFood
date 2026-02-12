package org.example.models;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "order_item_modification",
        indexes = {
                @Index(name = "idx_order_item_modification_restaurant_id", columnList = "restaurant_id")
        })
@Check(constraints = "(order_product_id IS NOT NULL AND order_combo_id IS NULL) OR (order_product_id IS NOT NULL AND order_combo_id IS NOT NULL)")
public class OrderItemModification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 38, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_product_id")
    private OrderProduct orderProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_combo_id")
    private OrderCombo orderCombo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "modification_template_id", nullable = false)
    private ModificationTemplate modificationTemplate;

    @Column(name = "restaurant_id")
    private Long restaurantId;



}
