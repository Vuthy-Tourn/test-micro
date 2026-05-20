package kh.edu.istad.stadoor.gateway.event.gateway;

import kh.edu.istad.stadoor.gateway.valueobject.gateway.GatewayDescription;
import kh.edu.istad.stadoor.common.valueobject.gateway.GatewayId;
import kh.edu.istad.stadoor.gateway.valueobject.gateway.GatewayName;
import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
public record GatewayUpdatedEvent(
        GatewayId gatewayId,
        GatewayName name,
        GatewayDescription description,
        ZonedDateTime updatedAt
) {
}