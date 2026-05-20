package kh.edu.istad.stadoor.common.valueobject.gateway;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record GatewayId(
        UUID gatewayId
) {

    @NotNull
    @Override
    public String toString() {
     return    gatewayId.toString();
    }
}
