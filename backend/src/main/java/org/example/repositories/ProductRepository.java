package org.example.repositories;

import org.example.models.Product;
import org.example.repositories.projections.ProductProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p " +
            "left JOIN FETCH p.semiProducts psp "+
            "left join FETCH psp.semiProduct "+
            "where p.id= :id ")
    Optional<Product> findWithSemiProductsById(@Param("id") Long id);


    @Query(" SELECT p FROM Product p " +
            " LEFT JOIN FETCH p.semiProducts psp "+
            " LEFT JOIN FETCH psp.semiProduct "+
            " WHERE p.restaurantId = :restaurantId ")
    List<Product> findAllWithSemiProductsByRestaurantId(Long restaurantId);

    @Query("SELECT p.id as id, p.name as name, p.price as price, p.category as category " +
            "FROM Product p WHERE p.id IN :ids ")
    Set<ProductProjection> findProductByIds(@Param("ids") Set<Long> ids);


    @Query("SELECT p FROM Product p WHERE p.restaurantId = :restaurantId")
    List<Product> findAllProductsByRestaurantId(Long restaurantId);

    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.restaurantId = :restaurantId")
    List<String> getProductCategories(@Param("restaurantId") Long restaurantId);

    @Query("SELECT p.id as id, p.name as name, p.price as price, p.category as category " +
            "FROM Product p WHERE p.id IN :id ")
    ProductProjection findProductById(@Param("id") Long id);
}
