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
        But backend converts it to 1g or 1ml for easier calculations later on
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

    @Column(name = "restaurant_id")
    private Long restaurantId;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public SemiProduct(String name, BigDecimal carbohydrate, BigDecimal fat, BigDecimal protein, UnitType unit, Long restaurantId) {
        this.name = name;
        this.carbohydrate = carbohydrate;
        this.fat = fat;
        this.protein = protein;
        this.unit = unit;
        this.restaurantId = restaurantId;
    }




}
