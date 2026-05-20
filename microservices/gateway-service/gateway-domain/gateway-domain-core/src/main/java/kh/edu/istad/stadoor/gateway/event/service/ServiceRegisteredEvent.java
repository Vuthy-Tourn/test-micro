package kh.edu.istad.stadoor.gateway.event.service;

import kh.edu.istad.stadoor.common.valueobject.gateway.GatewayId;
import kh.edu.istad.stadoor.gateway.valueobject.service.*;
import lombok.Builder;

import java.time.Instant;

@Builder
public record ServiceRegisteredEvent(
        ServiceId serviceId,
        GatewayId gatewayId,
        ServiceName name,
        ServiceType type,
        BaseUrl baseUrl,
        ServiceStatus status,
        Instant createdAt
//        ZonedDateTime updatedAt
) {
}