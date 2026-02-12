package org.example.models;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Table(name = "products",
        indexes = {
                @Index(name = "idx_products_restaurant_id", columnList = "restaurant_id")
        })
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SQLDelete(sql = "UPDATE products SET deleted_at = CURRENT_TIMESTAMP WHERE id=?")
@SQLRestriction("deleted_at IS NULL")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String category;

    private BigDecimal price;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductSemiProduct> semiProducts;

    @Column(name = "restaurant_id")
    private Long restaurantId;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column()
    private String type="SINGLE_PRODUCT";



    public void addSemiProduct(SemiProduct semiProduct, BigDecimal semiProductQuantity, Long restaurantId) {
       ProductSemiProduct productSemiProduct = new ProductSemiProduct(semiProduct,this, semiProductQuantity, restaurantId);
        this.semiProducts.add(productSemiProduct);
    }

    public Product(String name, String category, BigDecimal price, Set<ProductSemiProduct> semiProducts, Long restaurantId) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.semiProducts = semiProducts;
        this.restaurantId = restaurantId;
    }

    public Product(String name, String category, BigDecimal price, Long restaurantId) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.restaurantId = restaurantId;
    }

    public Product(Long id ,String name, String category, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
    }
}
