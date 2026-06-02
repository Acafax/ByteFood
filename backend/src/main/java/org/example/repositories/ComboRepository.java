package org.example.repositories;

import org.example.models.Combo;
import org.example.repositories.projections.ComboProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ComboRepository extends JpaRepository<Combo, Long> {

@Query("SELECT c.id AS id, c.name AS name, c.price AS price " +
       "FROM Combo c " +
       "WHERE c.id IN :ids")
    Set<ComboProjection> findCombosByIds(@Param("ids") Set<Long> ids);


    @Query(" SELECT c FROM Combo c " +
            "LEFT JOIN FETCH c.comboProducts cp  " +
            "WHERE c.restaurantId = :restaurantId") // Assuming restaurant ID is 1 for this example
    List<Combo> findAllByRestaurantId(@Param("restaurantId") Long restaurantId);

    @Query("SELECT c FROM Combo c " +
            "LEFT JOIN FETCH c.comboProducts cp " +
            "LEFT JOIN FETCH cp.product p " +
            "WHERE c.id = :id ")
    Optional<Combo> findWithDetailsById(Long id);
}
