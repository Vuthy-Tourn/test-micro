package kh.edu.istad.stadoor.gateway.route.dto;

import kh.edu.istad.stadoor.gateway.valueobject.route.HttpMethod;
import kh.edu.istad.stadoor.gateway.valueobject.route.RouteSecurity;

import java.time.Instant;
import java.util.UUID;

public record RouteResponse(
        UUID routeId,
        UUID gatewayId,
        UUID serviceId,
        ServiceInRouteResponse service,
        String path,
        HttpMethod method,
        String targetPath,
        RouteSecurity routeSecurity,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
}
