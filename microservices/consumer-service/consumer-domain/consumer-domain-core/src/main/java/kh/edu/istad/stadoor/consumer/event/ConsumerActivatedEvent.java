package kh.edu.istad.stadoor.consumer.event;

import kh.edu.istad.stadoor.consumer.valueobject.consumer.ConsumerStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record ConsumerActivatedEvent(
        UUID consumerId,
        ConsumerStatus status,
        Instant updatedAt
) {
}
