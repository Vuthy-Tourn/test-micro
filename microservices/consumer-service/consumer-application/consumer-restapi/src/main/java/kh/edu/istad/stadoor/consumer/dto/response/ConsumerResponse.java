package kh.edu.istad.stadoor.consumer.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ConsumerResponse(
        UUID consumerId,
        UUID tenantId,
        UUID gatewayId,
        String name,
        String description,
        String consumerType,
        String authType,
        String status,
        Instant createdAt
) {
}
