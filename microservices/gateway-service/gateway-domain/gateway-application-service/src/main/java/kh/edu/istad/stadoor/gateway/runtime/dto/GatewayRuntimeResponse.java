package kh.edu.istad.stadoor.gateway.runtime.dto;

import java.util.List;
import java.util.UUID;

public record GatewayRuntimeResponse(
        UUID gatewayId,
        UUID tenantId,
        String gatewayName,
        String gatewayType,
        String authType,
        String status,
        List<ServiceRuntimeItem> services,
        List<RouteRuntimeItem> routes
) {
}
