package org.example.kafka;

import lombok.extern.slf4j.Slf4j;
import org.example.dtos.order.OrderDto;
import org.example.services.StockItemService;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * Kafka listener for the {@code adjust-stock-item-amount} topic.
 * Consumes incoming orders and delegates stock quantity adjustment to {@link StockItemService}.
 *
 * @see KafkaScheduler
 */
@Service
@Slf4j
public class StockEventListener {

    private final StockItemService stockItemService;

    public StockEventListener(StockItemService stockItemService) {
        this.stockItemService = stockItemService;
    }

    /**
     * Processes a single order event from Kafka and adjusts stock quantities accordingly.
     *
     * @param orderDto     deserialized order payload
     * @param restaurantId Kafka message key identifying the restaurant
     */
    @RetryableTopic()
    @KafkaListener(topics = "adjust-stock-item-amount", groupId = "stock-management-group")
    public void kafkaAdjustStock(
        @Payload OrderDto orderDto,
        @Header(KafkaHeaders.RECEIVED_KEY) String restaurantId
    ) {
        try {
            stockItemService.adjustStockItemAmount(orderDto);
            log.info("Stock successfully adjusted for restaurant ID: {}", restaurantId);
        }catch (Exception e){
            log.error("Error during stock adjustment for restaurant ID: {}", restaurantId);
            throw e;
        }

    }

}
