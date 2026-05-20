package kh.edu.istad.stadoor.gateway.event.route;

import kh.edu.istad.stadoor.common.valueobject.gateway.GatewayId;
import kh.edu.istad.stadoor.gateway.valueobject.route.*;
import kh.edu.istad.stadoor.gateway.valueobject.service.ServiceId;
import lombok.Builder;

import java.time.Instant;

@Builder
public record RouteCreatedEvent(
        RouteId routeId,
        GatewayId gatewayId,
        ServiceId serviceId,
        RoutePath path,
        HttpMethod method,
        TargetPath targetPath,
        RouteSecurity routeSecurity,
        RouteStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}