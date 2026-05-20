package kh.edu.istad.stadoor.consumer.event;

import kh.edu.istad.stadoor.consumer.valueobject.auth.AuthType;
import kh.edu.istad.stadoor.consumer.valueobject.consumer.ConsumerStatus;
import kh.edu.istad.stadoor.consumer.valueobject.consumer.ConsumerType;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record ConsumerRegisteredEvent(
        UUID consumerId,
        UUID tenantId,
        UUID gatewayId,
        String name,
        String description,
        ConsumerType consumerType,
        AuthType authType,
        ConsumerStatus status,
        Instant createdAt,
        Instant updatedAt,

        UUID credentialId,
        String credentialType,
        String username,
        String credentialValue,
        String credentialHash,
        String algorithm,
        Integer accessTokenTtl,
        Integer refreshTokenTtl
) {}
