package kh.edu.istad.stdoor.eureka.event.valueObject;

import lombok.Builder;

import java.util.UUID;

@Builder
public record GatewayId(
        UUID gatewayId
) {

}
