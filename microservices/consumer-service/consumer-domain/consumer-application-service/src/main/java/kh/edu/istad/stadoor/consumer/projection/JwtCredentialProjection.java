package kh.edu.istad.stadoor.consumer.projection;

import java.util.UUID;

public record JwtCredentialProjection(
        UUID consumerId,
        UUID tenantId,
        UUID gatewayId,
        String credentialHash,
        UUID jwtCredentialId
) {
}
