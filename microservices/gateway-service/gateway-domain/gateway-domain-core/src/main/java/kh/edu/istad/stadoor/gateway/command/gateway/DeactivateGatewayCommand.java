

package kh.edu.istad.stadoor.gateway.command.gateway;

import kh.edu.istad.stadoor.common.valueobject.gateway.GatewayId;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

public record DeactivateGatewayCommand(
        @TargetAggregateIdentifier
        GatewayId gatewayId,
        UUID userId
) {
}
