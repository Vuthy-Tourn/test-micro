package kh.edu.istad.stadoor.gateway.runtime.model;

import java.util.List;
import java.util.UUID;

public record ServiceRuntimeConfig(
        UUID serviceId,
        UUID gatewayId,
        String name,
        String type,
        String baseUrl,
        String status,
        List<RouteRuntimeConfig> routes
) {
    public boolean active() {
        return "ACTIVE".equalsIgnoreCase(status);
    }
}
