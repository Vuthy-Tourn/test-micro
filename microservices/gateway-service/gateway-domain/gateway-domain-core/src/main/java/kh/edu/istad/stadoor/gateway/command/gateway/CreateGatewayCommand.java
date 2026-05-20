package kh.edu.istad.stadoor.gateway.command.gateway;

import kh.edu.istad.stadoor.common.valueobject.TenantId;
import kh.edu.istad.stadoor.common.valueobject.gateway.GatewayId;
import kh.edu.istad.stadoor.gateway.valueobject.gateway.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;


public record CreateGatewayCommand(
        @TargetAggregateIdentifier
        GatewayId gatewayId,
        TenantId tenantId,
        GatewayName name,
        GatewayDescription description,
        kh.edu.istad.stadoor.common.valueobject.gateway.GatewayStatus status,
        AuthType authType,
        GatewayType gatewayType
) {
}
