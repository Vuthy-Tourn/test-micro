package kh.edu.istad.stadoor.gateway.gateway.dto.query;

import java.time.LocalDate;
import java.util.List;

public record UserProfileResponse(
        String id,
        String username,
        String email,
        String roleId,
        List<String> clientIds,
        String tenantId,
        Boolean isActive,
        LocalDate dob,
        String profileImage
) {
}
