package org.example.repositories;

import org.example.models.OutboxStockEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxStockEventRepository extends JpaRepository<OutboxStockEvent, Long> {
    @Query("""
           SELECT e FROM OutboxStockEvent e
           WHERE e.status = "PENDING" 
        """ )
    List<OutboxStockEvent> findByStatusPending();
}
