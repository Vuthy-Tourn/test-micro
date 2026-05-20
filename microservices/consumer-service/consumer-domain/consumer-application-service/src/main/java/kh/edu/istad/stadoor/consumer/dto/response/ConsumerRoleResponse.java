package kh.edu.istad.stadoor.consumer.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ConsumerRoleResponse(
        UUID id,
        UUID tenantId,
        String name,
        String description,
        Boolean status,
        Instant createdAt
) {
}
