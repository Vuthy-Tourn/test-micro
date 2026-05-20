
package kh.edu.istad.stadoor.gateway.event.gateway;

import kh.edu.istad.stadoor.common.valueobject.gateway.GatewayId;
import kh.edu.istad.stadoor.common.valueobject.gateway.GatewayStatus;
import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
public record GatewayActivatedEvent(
        GatewayId gatewayId,
        GatewayStatus status,
        ZonedDateTime updatedAt
) {
}