package org.example.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@SQLDelete(sql = "UPDATE restaurants SET deleted_at = CURRENT_TIMESTAMP WHERE id=?")
@SQLRestriction("deleted_at IS NULL")
@Table(name = "restaurants")
public class Restaurant {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "stock_id",referencedColumnName = "id")
    private Stock restaurantStock;

    @OneToMany(mappedBy = "restaurant")
    List<User> staff;




}
