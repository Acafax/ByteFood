package org.example.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.cglib.proxy.LazyLoader;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "stock_item_dictionaries",
        indexes = {
                @Index(name = "idx_stock_semi_products_restaurant_id", columnList = "restaurant_id")
        })
@SQLDelete(sql="UPDATE stock_item_dictionaries SET deleted_at = current_timestamp WHERE id=?")
@SQLRestriction("deleted_at IS NULL")
public class StockItemDictionary {
    // Table defining container types like "Flour 25kg bag", "Sugar 1kg bag" "Carton of lettuce" etc.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private UnitType unit;

    @Column(name = "restaurant_id")
    private Long restaurantId;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Multiple defining how much of the semiProduct is contained in this stock item dictionary ( multiple * semiProduct unit | 7000 * 1G = 7000 G)
    @Column(precision = 10, scale = 3)
    private BigDecimal multipleOfSemiProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semi_product_id", referencedColumnName = "id")
    private SemiProduct semiProduct;
}
