package kh.edu.istad.stadoor.gateway.runtime.model;

import java.util.List;
import java.util.UUID;
public record GatewayRuntimeConfig(
        UUID gatewayId,
        UUID tenantId,
        String gatewayName,
        String description,
        String gatewayType,
        String authType,
        String status,
        List<ServiceRuntimeConfig> services
) {
    public boolean active() {
        return "ACTIVE".equalsIgnoreCase(status);
    }
}
