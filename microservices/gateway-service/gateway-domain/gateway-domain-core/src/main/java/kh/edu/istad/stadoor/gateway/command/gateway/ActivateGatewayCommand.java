
package kh.edu.istad.stadoor.gateway.command.gateway;

import kh.edu.istad.stadoor.common.valueobject.gateway.GatewayId;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record ActivateGatewayCommand(

        @TargetAggregateIdentifier
        GatewayId gatewayId
) {
}
