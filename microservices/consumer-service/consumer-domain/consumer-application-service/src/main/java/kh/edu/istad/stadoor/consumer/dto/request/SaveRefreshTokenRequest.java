package kh.edu.istad.stadoor.consumer.dto.request;

import java.time.Instant;
import java.util.UUID;

public record SaveRefreshTokenRequest(
        UUID id,
        UUID jwtCredentialId,
        String tokenHash,
        Instant expiresAt,
        Instant createdAt
) {
}
