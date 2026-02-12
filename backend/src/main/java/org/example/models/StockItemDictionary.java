package org.example.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "stock_item_dictionaries",
        indexes = {
                @Index(name = "idx_stock_semi_products_restaurant_id", columnList = "restaurant_id")
        })
public class StockItemDictionary {
    // Table defining container types like "Flour 25kg bag", "Sugar 1kg bag" "Carton of lettuce" etc.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    // Minimal stock quantity to trigger UI alert in stock management page
    @Column(precision = 10, scale = 3)
    private BigDecimal minimalStockQuantity;

    @Enumerated(EnumType.STRING)
    private UnitType unit;

    @Column(name = "restaurant_id")
    private Long restaurantId;

    // Quantity defining how much of the child item is contained in this stock item dictionary entry ( childQuantity=6 and childItem=<lettuce_id> )
    @Column(precision = 10, scale = 3)
    private BigDecimal childQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", referencedColumnName = "id")
    private StockItemDictionary childItem;

    @ManyToOne
    @JoinColumn(name = "semi_product_id", referencedColumnName = "id")
    SemiProduct semiProduct;
}
