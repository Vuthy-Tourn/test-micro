package kh.edu.istad.stadoor.consumer.valueobject.credential;

import java.time.Instant;
import java.util.UUID;

/** Parent credential record — one per consumer. */
public record ConsumerCredential(
        UUID id,
        UUID consumerId,
        UUID tenantId,
        String credentialType,
        boolean status,
        Instant createdAt,
        Instant updatedAt
) {
    public static ConsumerCredential of(UUID consumerId, UUID tenantId, String credentialType) {
        Instant now = Instant.now();
        return new ConsumerCredential(UUID.randomUUID(), consumerId, tenantId,
                credentialType, true, now, now);
    }
}
