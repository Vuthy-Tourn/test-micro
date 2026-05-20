package kh.edu.istad.stadoor.gateway.event.gateway;

import kh.edu.istad.stadoor.common.valueobject.TenantId;
import kh.edu.istad.stadoor.common.valueobject.gateway.GatewayId;
import kh.edu.istad.stadoor.common.valueobject.gateway.GatewayStatus;
import kh.edu.istad.stadoor.gateway.valueobject.gateway.*;
import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
public record GatewayCreatedEvent(
        GatewayId gatewayId,
        TenantId tenantId,
        GatewayName name,
        GatewayDescription description,
        GatewayType gatewayType,
        AuthType authType,
        GatewayStatus status,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt
) {
}
