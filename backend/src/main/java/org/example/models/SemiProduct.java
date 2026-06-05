package org.example.models;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "semi_products",
        indexes = {
                @Index(name = "idx_semi_products_restaurant_id", columnList = "restaurant_id")
        })
@Entity
@Setter
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE semi_products SET deleted_at = CURRENT_TIMESTAMP WHERE id=?")
@SQLRestriction("deleted_at IS NULL")
public class SemiProduct {
    /*
        User in the frontend will input nutritional values defined per 100 g or 100 ml of product.
        But backend converts it to 1g or 1ml for easier calculations later on.

        If something is product by it own it have to be defined in semi Product.
        Coca Cola 500ml is product but have to be defined in semi Product for Stock calculations
    */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private BigDecimal carbohydrate;

    private BigDecimal fat;

    private BigDecimal protein;

    @Enumerated(EnumType.STRING)
    private UnitType unit;

    // section to differentiate product change modification
    // (subCategory: "Patties, Fries": )
    @ManyToOne
    @JoinColumn(name = "subcategory")
    private Subcategory subcategory;

    // Minimal stock quantity to trigger UI alert in stock management page
    @Column(precision = 10, scale = 3)
    private BigDecimal minimalStockQuantity;

    @Column(name = "restaurant_id")
    private Long restaurantId;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public SemiProduct(String name, BigDecimal carbohydrate, BigDecimal fat, BigDecimal protein, UnitType unit, BigDecimal minimalStockQuantity, Long restaurantId) {
        this.name = name;
        this.carbohydrate = carbohydrate;
        this.fat = fat;
        this.protein = protein;
        this.unit = unit;
        this.restaurantId = restaurantId;
    }

    public SemiProduct(Long id, String name, BigDecimal carbohydrate, BigDecimal fat, BigDecimal protein, UnitType unit, BigDecimal minimalStockQuantity) {
        this.id = id;
        this.name = name;
        this.carbohydrate = carbohydrate;
        this.fat = fat;
        this.protein = protein;
        this.unit = unit;
    }
}
