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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {


    private final OrderService orderService;


    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/")
    @PreAuthorize("@securityService.authentifyApiKey(#apiKey)")
    ResponseEntity<OrderDto> receiveOrder (
            @RequestHeader(value = "X-API-KEY") String apiKey,
            @Valid @RequestBody CreateOrderDTORequest dto)
    {
        OrderDto orderDto = orderService.saveOrder(dto);
        return ResponseEntity.ok(orderDto);
    }


}
