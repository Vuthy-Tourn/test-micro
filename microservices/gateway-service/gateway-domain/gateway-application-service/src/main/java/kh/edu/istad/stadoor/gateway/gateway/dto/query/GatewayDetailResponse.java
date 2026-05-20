package kh.edu.istad.stadoor.gateway.gateway.dto.query;

import java.time.ZonedDateTime;
import java.util.UUID;

public record GatewayDetailResponse(
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
