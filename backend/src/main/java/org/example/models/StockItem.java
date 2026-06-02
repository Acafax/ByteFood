package org.example.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "stock_items",
indexes = {
        @Index(name = "idx_stock_items_restaurant_id", columnList = "restaurant_id")
})
@SQLDelete(sql ="UPDATE stock_items SET deleted_at = CURRENT_TIMESTAMP WHERE id=? ")
@SQLRestriction("deleted_at IS NULL")
public class StockItem {
    //Table defining actual amount of items in stock, linked to SemiProduct defining the type of item
    // quantity is expressed in the base unit of the SemiProduct if stock has 7000g of tomato quantity = 7000 and SemiProduct is tomato.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 10, scale = 2)
    private BigDecimal purchasePrice;

    @Column(precision = 10, scale = 3)
    private BigDecimal quantity;

    private LocalDateTime expirationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semi_product_id", referencedColumnName = "id")
    private SemiProduct semiProduct;

    @ManyToOne
    @JoinColumn(name = "stock_id", referencedColumnName = "id")
    private Stock stock;

    @Column(name = "restaurant_id")
    private Long restaurantId;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public Long getSemiProductId(){
        return this.getSemiProduct().getId();
    }
}
