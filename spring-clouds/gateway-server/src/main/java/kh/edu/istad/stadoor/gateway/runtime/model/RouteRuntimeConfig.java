package kh.edu.istad.stadoor.gateway.runtime.model;

import java.util.UUID;

public record RouteRuntimeConfig(
        UUID routeId,
        UUID gatewayId,
        UUID serviceId,
        String path,
        String targetPath,
        String method,
        String routeSecurity,
        String status
) {
    public boolean secured() {
        return "SECURE".equalsIgnoreCase(routeSecurity)
                || "SECURED".equalsIgnoreCase(routeSecurity);
    }

    public boolean active() {
        return "ACTIVE".equalsIgnoreCase(status);
    }
}
