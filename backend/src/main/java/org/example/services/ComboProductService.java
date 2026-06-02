package org.example.services;

import org.example.repositories.OrderComboRepository;
import org.springframework.stereotype.Service;

@Service 
public class ComboProductService {


    private final OrderComboRepository orderComboRepository;

    public ComboProductService(OrderComboRepository orderComboRepository) {
        this.orderComboRepository = orderComboRepository;
    }



}
