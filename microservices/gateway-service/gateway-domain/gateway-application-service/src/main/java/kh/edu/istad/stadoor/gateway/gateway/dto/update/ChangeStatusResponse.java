package kh.edu.istad.stadoor.gateway.gateway.dto.update;

import kh.edu.istad.stadoor.common.valueobject.gateway.GatewayStatus;

import java.util.UUID;

public record ChangeStatusResponse(
        UUID gatewayId,
        String message,
        GatewayStatus status,
        String email
) {
}
