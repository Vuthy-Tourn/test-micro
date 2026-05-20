package kh.edu.istad.stadoor.gateway.gateway.dto.query;

import java.time.ZonedDateTime;
import java.util.UUID;

public record GatewaySummaryResponse(
        UUID gatewayId,
        UUID tenantId,
        String gatewayName,
        String description,
        String gatewayType,
        String authType,
        String status,
        Long serviceCount,
        Long routeCount,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt
) {
}
