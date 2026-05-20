package kh.edu.istad.stadoor.common.event.gateway;

import kh.edu.istad.stadoor.common.valueobject.gateway.GatewayId;
import kh.edu.istad.stadoor.common.valueobject.gateway.GatewayStatus;
import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.UUID;

@Builder
public record GatewayDeactivatedEvent(
        GatewayId gatewayId,
        UUID userId,
        // String email,
        GatewayStatus status,
        ZonedDateTime updatedAt
) {
}