package kh.edu.istad.stadoor.gateway.runtime.dto;

import java.util.UUID;

public record RouteRuntimeItem(
        UUID routeId,
        UUID gatewayId,
        UUID serviceId,
        String path,
        String method,
        String targetPath,
        String routeSecurity,
        String status
) {
}
