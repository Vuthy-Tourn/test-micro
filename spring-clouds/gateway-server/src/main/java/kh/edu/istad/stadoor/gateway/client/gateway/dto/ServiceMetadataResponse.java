package kh.edu.istad.stadoor.gateway.client.gateway.dto;

import java.util.UUID;

public record ServiceMetadataResponse(
        UUID serviceId,
        UUID gatewayId,
        String name,
        String type,
        String baseUrl,
        String status
) {
}
