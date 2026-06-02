package org.example.dtos.outboxStockEvent;

public record CreateOutboxEventDTO(String topic, String aggregationId, String payload) {
}
