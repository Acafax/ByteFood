package org.example.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Primary;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "outbox_stock_event")
public class OutboxStockEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String topic;

    private String aggregateId; // Key

    @Column(columnDefinition = "TEXT")
    private String payload; //JSON with exact order

    @Enumerated(EnumType.STRING)
    private EventStatus status = EventStatus.PENDING;


    private LocalDateTime createdAt = LocalDateTime.now();

    public enum EventStatus {
        PENDING , // PENDING = waiting to be published to Kafka by Scheduler
        SENT     // SENT = successfully published to Kafka (broker acknowledged receipt)

    }

}
