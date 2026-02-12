package org.example.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products_semi_products",
        indexes = {
                @Index(name = "idx_products_semi_products_restaurant_id", columnList = "restaurant_id")
        })
public class ProductSemiProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "semi_product_id", referencedColumnName = "id")
    private SemiProduct semiProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    // Quantity is defined in 1g quantity=20 equals 20 grams
    private BigDecimal quantity;

    @Column(name = "restaurant_id")
    private Long restaurantId;

    public ProductSemiProduct(SemiProduct semiProduct, Product product, BigDecimal quantity, Long restaurantId) {
        this.semiProduct = semiProduct;
        this.product = product;
        this.quantity = quantity;
        this.restaurantId = restaurantId;
    }
}
