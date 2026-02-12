package org.example.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
public class StockItem {
    //Table defining actual items in stock, linked to stock_item_dictionary defining the type of item
    // quantity is multiple of the quantity defined in stock_item_dictionary
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(precision = 10, scale = 2)
    BigDecimal purchasePrice;

    @Column(precision = 10, scale = 3)
    BigDecimal quantity;

    LocalDateTime expirationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_item_dictionary_id", referencedColumnName = "id")
    StockItemDictionary stockItemDictionary;

    @ManyToOne
    @JoinColumn(name = "stock_id", referencedColumnName = "id")
    Stock stock;

    @Column(name = "restaurant_id")
    Long restaurantId;


}
