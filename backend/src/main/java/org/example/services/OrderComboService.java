package org.example.services;

import org.example.repositories.OrderComboRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderComboService {


    private final OrderComboRepository orderComboRepository;

    public OrderComboService(OrderComboRepository orderComboRepository) {
        this.orderComboRepository = orderComboRepository;
    }
}
