package org.example.dtos.outboxStockEvent;

import org.example.models.OutboxStockEvent.EventStatus;

import java.time.LocalDateTime;

public record OutboxStockEventDTO(Long id, String topic, String aggregationId, String payload, EventStatus eventStatus, LocalDateTime createdAt) {
}
