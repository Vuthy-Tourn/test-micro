package kh.edu.istad.stadoor.gateway.client.gateway.dto;

import java.util.UUID;

public record RouteMetadataResponse(
        UUID routeId,
        UUID gatewayId,
        UUID serviceId,
        String path,
        String targetPath,
        String method,
        String routeSecurity,
        String status
) {
}

