package kh.edu.istad.stadoor.consumer.dto.request;

import java.util.UUID;

public record CreateRoleInput(
        UUID tenantId,
        String name,
        String description
) {
}
