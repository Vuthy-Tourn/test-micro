package kh.edu.istad.stadoor.consumer.command;

import kh.edu.istad.stadoor.consumer.valueobject.auth.AuthType;
import kh.edu.istad.stadoor.consumer.valueobject.consumer.ConsumerType;
import lombok.Builder;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.time.Instant;
import java.util.UUID;

@Builder
public record RegisterConsumerCommand(

        @TargetAggregateIdentifier
        UUID consumerId,

        UUID tenantId,
        UUID gatewayId,
        String name,
        String description,
        ConsumerType consumerType,
        AuthType authType,
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
