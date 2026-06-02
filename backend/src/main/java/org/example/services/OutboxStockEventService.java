package org.example.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.dtos.order.OrderDto;
import org.example.models.OutboxStockEvent;
import org.example.repositories.OutboxStockEventRepository;
import org.example.security.CustomUserDetailsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class OutboxStockEventService {


    private final OutboxStockEventRepository outboxStockEventRepository;
    private final ObjectMapper objectMapper;
    private final CustomUserDetailsService customUserDetailsService;

    public OutboxStockEventService(OutboxStockEventRepository outboxStockEventRepository, ObjectMapper jacksonObjectMapper, CustomUserDetailsService customUserDetailsService) {
        this.outboxStockEventRepository = outboxStockEventRepository;
        this.objectMapper = jacksonObjectMapper;
        this.customUserDetailsService = customUserDetailsService;
    }

    public void addToOutbox(OrderDto orderDto ){
        try {
            String restaurantId = customUserDetailsService.getCurrentRestaurantId().toString();
            String orderJons = objectMapper.writeValueAsString(orderDto);

            OutboxStockEvent outboxStockEvent = new OutboxStockEvent();
            outboxStockEvent.setTopic("adjust-stock-item-amount");
            outboxStockEvent.setAggregateId(restaurantId);
            outboxStockEvent.setPayload(orderJons);
            outboxStockEvent.setCreatedAt(LocalDateTime.now());

            outboxStockEventRepository.save(outboxStockEvent);

            log.info("order added to outboxEvent table");
        }catch (JsonProcessingException e){
            throw new RuntimeException("Serialization Error OrderDto to JSON", e);

        }

    }


    public List<OutboxStockEvent> getEvents() {
        return outboxStockEventRepository.findByStatusPending();
    }

    public void changeStatus(OutboxStockEvent event) {
        event.setStatus(OutboxStockEvent.EventStatus.SENT);
        outboxStockEventRepository.save(event);
    }
}
