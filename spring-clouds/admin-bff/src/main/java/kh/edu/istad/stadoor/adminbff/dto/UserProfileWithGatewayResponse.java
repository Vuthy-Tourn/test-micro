package kh.edu.istad.stadoor.adminbff.dto;

import java.util.List;

public record UserProfileWithGatewayResponse(
        String id,
        String username,
        String email,
        String roleId,
        String profileImage,
        List<String> clientIds,
        String tenantId,
        List<GatewayInfo> gateways
) {
    public record GatewayInfo(String gatewayName) {}
}
