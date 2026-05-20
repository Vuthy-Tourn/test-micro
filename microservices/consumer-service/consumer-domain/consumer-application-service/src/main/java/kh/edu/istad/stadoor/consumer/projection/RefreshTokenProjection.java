package kh.edu.istad.stadoor.consumer.projection;

import java.util.UUID;

public record RefreshTokenProjection(
        UUID consumerId,
        UUID tenantId,
        UUID gatewayId,
        String username,
        UUID jwtCredentialId
) {
}
