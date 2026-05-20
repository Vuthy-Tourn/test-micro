package kh.edu.istad.stadoor.consumer.valueobject.credential;

import java.time.Instant;
import java.util.UUID;

public record BasicAuthCredential(
        UUID credentialId,
        UUID consumerId,
        UUID tenantId,
        String username,
        String passwordHash,
        Instant createdAt
) {}
