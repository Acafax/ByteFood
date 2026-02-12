package org.example.repositories;

import org.example.models.OrderItemModification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemModificationRepository extends JpaRepository<OrderItemModification, Long> {
}
