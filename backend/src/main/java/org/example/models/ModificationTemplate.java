package org.example.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "modification_template",
        indexes = {
                @Index(name = "idx_modification_template_restaurant_id", columnList = "restaurant_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SQLDelete(sql = "UPDATE modification_template SET deleted_at = CURRENT_TIMESTAMP WHERE id=?")
@SQLRestriction("deleted_at IS NULL")
public class ModificationTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private BigDecimal price;

    private String category;

    @ManyToOne
    @JoinColumn(name = "modificator")
    private SemiProduct semiProduct;


    @Column(name = "restaurant_id")
    private Long restaurantId;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;


    public ModificationTemplate(String name, BigDecimal price, String category, SemiProduct semiProduct) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.semiProduct=semiProduct;
    }
    public ModificationTemplate(String name, BigDecimal price, String category) {
        this.name = name;
        this.price = price;
        this.category = category;
    }

}
