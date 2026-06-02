package org.example.repositories;

import org.example.models.ProductSemiProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductSemiProductRepository extends JpaRepository<ProductSemiProduct, Long> {
}
