package kh.edu.istad.stadoor.gateway.gateway.dto.create;

import java.time.ZonedDateTime;
import java.util.UUID;

public record CreateGatewayResponse(
        UUID gatewayId,
        UUID tenantId,
        String gatewayName,
        String description,
        String gatewayType,
        String authType,
        String status,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt
) {
}
