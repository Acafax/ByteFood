package org.example.controllers;


import jakarta.validation.Valid;
import org.example.dtos.order.CreateOrderDTORequest;
import org.example.dtos.order.OrderDto;
import org.example.dtos.order.OrderProductDTO;
import org.example.dtos.order.OrderProductRequestDTO;
import org.example.models.*;
import org.example.repositories.ModificationTemplateRepository;
import org.example.services.ComboService;
import org.example.services.OrderService;
import org.example.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {


    private final OrderService orderService;
    private final ProductService productService;
    private final ComboService comboService;
    private final ModificationTemplateRepository modificationTemplateRepository;


    public OrderController(OrderService orderService, ProductService productService, ComboService comboService, ModificationTemplateRepository modificationTemplateRepository) {
        this.orderService = orderService;
        this.productService = productService;
        this.comboService = comboService;
        this.modificationTemplateRepository = modificationTemplateRepository;
    }

    @PostMapping("/")
    ResponseEntity<OrderDto> receiveOrder ( @Valid @RequestBody CreateOrderDTORequest dto){
        OrderDto orderDto = orderService.saveOrder(dto);
        return ResponseEntity.ok(orderDto);
    }


}
