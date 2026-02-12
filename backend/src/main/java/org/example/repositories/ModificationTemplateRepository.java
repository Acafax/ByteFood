package org.example.repositories;

import org.example.models.ModificationTemplate;
import org.example.repositories.projections.ModificationTemplateProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ModificationTemplateRepository extends JpaRepository<ModificationTemplate, Long> {

    @Query(" SELECT m.id AS id, m.name AS name, m.price AS price, m.category AS category, sp.id AS semiProductId ,m.restaurantId AS restaurantId " +
           "FROM ModificationTemplate m " +
            "LEFT JOIN m.semiProduct sp " +
           "WHERE m.id IN :ids")
    Set<ModificationTemplateProjection> findModificationTemplateByIds(@Param("ids") Set<Long> ids);


    @Query(" SELECT m FROM ModificationTemplate m " +
            " LEFT JOIN FETCH m.semiProduct sp " +
           "WHERE m.restaurantId = :restaurantId")
    List<ModificationTemplate> findAllWithSemiProductByRestaurantId(@Param("restaurantId") Long restaurantId);

    @Query(" SELECT m.id AS id, m.name AS name, m.price AS price, m.category AS category, sp.id AS semiProductId ,m.restaurantId AS restaurantId " +
            "FROM ModificationTemplate m " +
            "LEFT JOIN m.semiProduct sp " +
            "WHERE m.id =:id")
    Optional<ModificationTemplateProjection> findModificationTemplateById(@Param("id") Long id);

}
