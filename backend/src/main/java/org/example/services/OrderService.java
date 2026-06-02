package org.example.services;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.dtos.assemblers.OrderAssembler;
import org.example.dtos.order.CreateOrderDTORequest;
import org.example.dtos.order.OrderDto;
import org.example.models.Order;
import org.example.repositories.OrderRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderService {


    private final OrderRepository orderRepository;
    private final OrderAssembler orderAssembler;
    private final OutboxStockEventService outboxStockEventService;


    public OrderService(OrderRepository orderRepository, OrderAssembler orderAssembler, OutboxStockEventService outboxStockEventService) {
        this.orderRepository = orderRepository;
        this.orderAssembler = orderAssembler;
        this.outboxStockEventService = outboxStockEventService;
    }

    @Transactional
    public OrderDto saveOrder(CreateOrderDTORequest createOrderDTORequest){
        Order order = orderAssembler.assembleOrder(createOrderDTORequest);
        Order save = orderRepository.save(order);
        OrderDto orderDto = orderAssembler.assembleOrderDto(save);
        outboxStockEventService.addToOutbox(orderDto);
        return orderDto;
    }
}
