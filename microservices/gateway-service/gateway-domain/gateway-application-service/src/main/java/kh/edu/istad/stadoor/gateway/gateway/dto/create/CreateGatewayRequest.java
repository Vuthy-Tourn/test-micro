package kh.edu.istad.stadoor.gateway.gateway.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kh.edu.istad.stadoor.gateway.valueobject.gateway.AuthType;
import kh.edu.istad.stadoor.common.valueobject.gateway.GatewayStatus;
import kh.edu.istad.stadoor.gateway.valueobject.gateway.GatewayType;

public record CreateGatewayRequest(
        @NotBlank
        String gatewayName,

        @NotBlank
        String description,

        @NotNull
        GatewayType gatewayType,

        @NotNull
        AuthType authType,

        GatewayStatus status
) {
}
