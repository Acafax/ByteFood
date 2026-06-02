package org.example.services;

import org.example.repositories.OrderProductRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderProductService{


    private final OrderProductRepository orderProductRepository;

    public OrderProductService(OrderProductRepository orderProductRepository) {
        this.orderProductRepository = orderProductRepository;
    }
}
