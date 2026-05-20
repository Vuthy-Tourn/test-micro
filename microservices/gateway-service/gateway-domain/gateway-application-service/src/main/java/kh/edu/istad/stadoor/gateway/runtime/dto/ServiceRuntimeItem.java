package kh.edu.istad.stadoor.gateway.runtime.dto;

import java.util.UUID;

public record ServiceRuntimeItem(
        UUID serviceId,
        UUID gatewayId,
        String name,
        String type,
        String baseUrl,
        String status
) {
}
