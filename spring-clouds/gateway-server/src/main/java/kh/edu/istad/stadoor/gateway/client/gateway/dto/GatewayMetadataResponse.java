package kh.edu.istad.stadoor.gateway.client.gateway.dto;

import java.util.List;
import java.util.UUID;

public record GatewayMetadataResponse(
        UUID gatewayId,
        UUID tenantId,
        String gatewayName,
        String description,
        String gatewayType,
        String authType,
        String status,
        List<ServiceMetadataResponse> services,
        List<RouteMetadataResponse> routes
) {
}
