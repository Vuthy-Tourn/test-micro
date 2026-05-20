package kh.edu.istad.stadoor.gateway.command.gateway;


import kh.edu.istad.stadoor.gateway.valueobject.gateway.GatewayDescription;
import kh.edu.istad.stadoor.common.valueobject.gateway.GatewayId;
import kh.edu.istad.stadoor.gateway.valueobject.gateway.GatewayName;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record UpdateGatewayCommand(

        @TargetAggregateIdentifier
        GatewayId gatewayId,
        GatewayName name,
        GatewayDescription description
) {
}
