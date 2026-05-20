package kh.edu.istad.stadoor.consumer.valueobject.credential;

import java.time.Instant;
import java.util.UUID;

public record ApiKeyCredential(
        UUID credentialId,
        UUID consumerId,
        UUID tenantId,
        String apiKey,
        String keyHash,
        Instant createdAt
) {}
