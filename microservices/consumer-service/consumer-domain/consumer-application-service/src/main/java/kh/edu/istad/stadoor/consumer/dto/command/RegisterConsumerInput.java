package kh.edu.istad.stadoor.consumer.dto.command;

import kh.edu.istad.stadoor.consumer.valueobject.auth.AuthType;
import kh.edu.istad.stadoor.consumer.valueobject.consumer.ConsumerType;
import lombok.Builder;

import java.util.UUID;

@Builder
public record RegisterConsumerInput(
        UUID gatewayId,
        UUID tenantId,
        String name,
        String description,
        ConsumerType consumerType,
        AuthType authType,
        String username,
        String password
) {}
