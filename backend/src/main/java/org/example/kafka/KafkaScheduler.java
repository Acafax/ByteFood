package org.example.kafka;

import lombok.extern.slf4j.Slf4j;
import org.example.models.OutboxStockEvent;
import org.example.services.OutboxStockEventService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Scheduler publishing pending stock events from the outbox table to Kafka every 5 seconds.
 * Marks each event as processed after successful delivery.
 *
 * @see StockEventListener
 * @see OutboxStockEventService
 */
@Service
@Slf4j
public class KafkaScheduler {

    private final KafkaTemplate<String, String > kafkaTemplate;
    private final OutboxStockEventService outboxStockEventService;

    public KafkaScheduler(KafkaTemplate<String, String> kafkaTemplate, OutboxStockEventService outboxStockEventService) {
        this.kafkaTemplate = kafkaTemplate;
        this.outboxStockEventService = outboxStockEventService;
    }

    /**
     * Fetches all pending outbox events and sends them to the Kafka topic.
     * Each successfully sent event is marked as processed.
     */
    @Scheduled(fixedDelay = 5000)
    public void processOutboxEvents(){
        List<OutboxStockEvent> events = outboxStockEventService.getEvents();

        if (events!=null){
            events.forEach(event -> {
                try {
                    kafkaTemplate.send(event.getTopic(), event.getAggregateId() ,event.getPayload());
                    outboxStockEventService.changeStatus(event);
                    log.info("Stock event added to kafka ID {} | restaurant_id = {}" , event.getId(), event.getAggregateId());
                }catch (Exception e){
                    log.error("Failed to send event ID: {}", event.getId());
                    throw e;
                }
            });
        }
    }
}
