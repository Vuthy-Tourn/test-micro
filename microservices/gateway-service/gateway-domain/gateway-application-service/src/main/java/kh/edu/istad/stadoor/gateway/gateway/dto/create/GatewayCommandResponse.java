package kh.edu.istad.stadoor.gateway.gateway.dto.create;

import java.util.UUID;

public record GatewayCommandResponse(
        UUID gatewayId,
        String message
) {
}
