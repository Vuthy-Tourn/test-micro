package kh.edu.istad.stadoor.consumer.dto.request;

import java.util.UUID;

public record AssignRoleInput(
        UUID consumerId,
        UUID roleId,
        UUID assignedByUserId
) {
}
