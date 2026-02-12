package org.example.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "combo_product",
        indexes = {
                @Index(name = "idx_combo_product_restaurant_id", columnList = "restaurant_id")
        })
@Setter
@Getter
@NoArgsConstructor
@Entity
public class ComboProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "combo_id", referencedColumnName = "id")
    private Combo combo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @Column(name = "restaurant_id")
    private Long restaurantId;

    public ComboProduct(Integer quantity, Combo combo, Product product, Long restaurantId) {
        this.quantity = quantity;
        this.combo = combo;
        this.product = product;
        this.restaurantId = restaurantId;
    }
}
