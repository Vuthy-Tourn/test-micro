package kh.edu.istad.stadoor.adminbff.dto;

import java.util.List;

public record UserProfileResponse(
        String id,
        String username,
        String email,
        String roleId,
        List<String> clientIds,
        String tenantId,
        String profileImage
){
}
