package kh.edu.istad.stadoor.consumer.valueobject.credential;

import java.time.Instant;
import java.util.UUID;

public record JwtCredential(
        UUID credentialId,
        UUID consumerId,
        UUID tenantId,
        String username,
        String passwordHash,
        String secretKey,
        String algorithm,
        int accessTokenTtl,
        int refreshTokenTtl,
        Instant createdAt
) {}
