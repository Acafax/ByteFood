package org.example.services;

import org.example.repositories.OrderItemModificationRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderItemModificationService {


    private final OrderItemModificationRepository orderItemModificationRepository;

    public OrderItemModificationService(OrderItemModificationRepository orderItemModificationRepository) {
        this.orderItemModificationRepository = orderItemModificationRepository;
    }
}
