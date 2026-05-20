package kh.edu.istad.stadoor.consumer.dto.response;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record RegisterConsumerResponse(
        UUID consumerId,
        UUID gatewayId,
        UUID tenantId,
        String name,
        boolean status,
        Instant createdAt,
        ConsumerCredentialResponse credential
) {}
