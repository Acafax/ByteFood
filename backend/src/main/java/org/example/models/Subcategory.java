package org.example.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(
        name = "subcategories",
        indexes = {
            @Index(name = "idx_subcategories_restaurant_id", columnList = "restaurant_id")
        }
)
/**
 * The Subcategory entity classifies products into specific groups.
 * This segregation ensures that only compatible modifications can be applied to a product.
 * For example, it prevents applying a burger modifier (like "extra cheese") to a drink (like "soda").
 */
public class Subcategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String subcategoryName;

    @Column(name = "restaurant_id")
    Long restaurantId;

    public Subcategory(String subcategoryName ) {
        this.subcategoryName = subcategoryName;
    }
}
