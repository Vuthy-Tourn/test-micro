package kh.edu.istad.stadoor.gateway.gateway.dto.update;

import jakarta.validation.constraints.NotBlank;

public record UpdateGatewayRequest(
        @NotBlank
        String gatewayName,

        @NotBlank
        String description
) {
}
