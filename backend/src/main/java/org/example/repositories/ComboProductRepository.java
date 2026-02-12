package org.example.repositories;

import org.example.models.ComboProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComboProductRepository extends JpaRepository<ComboProduct, Long> {
}
