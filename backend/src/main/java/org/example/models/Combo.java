package org.example.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Table(name = "combos",
indexes = {
        @Index(name = "idx_combos_restaurant_id", columnList = "restaurant_id")
})
@Setter
@Getter
@NoArgsConstructor
@Entity
@SQLDelete(sql = "UPDATE combos SET deleted_at = CURRENT_TIMESTAMP WHERE id=?")
@SQLRestriction("deleted_at IS NULL")
public class Combo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private BigDecimal price;

    @OneToMany(mappedBy = "combo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ComboProduct> comboProducts = new HashSet<>();

    @OneToMany(mappedBy = "combo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderCombo> orderCombo;

    @Column(name = "restaurant_id")
    private Long restaurantId;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column
    private String type="COMBO";
}
