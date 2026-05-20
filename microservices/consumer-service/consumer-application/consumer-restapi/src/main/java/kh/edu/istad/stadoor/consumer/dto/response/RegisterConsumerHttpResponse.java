package kh.edu.istad.stadoor.consumer.dto.response;

import java.time.Instant;
import java.util.UUID;

public record RegisterConsumerHttpResponse(
        UUID consumerId,
        UUID gatewayId,
        UUID tenantId,
        String name,
        boolean status,
        Instant createdAt,
        ConsumerCredentialResponse credential
) {}
