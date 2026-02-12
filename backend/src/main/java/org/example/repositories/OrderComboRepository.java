package org.example.repositories;

import org.example.models.OrderCombo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderComboRepository extends JpaRepository<OrderCombo, Long> {
}
