package kh.edu.istad.stadoor.gateway.command.service;

import kh.edu.istad.stadoor.common.valueobject.gateway.GatewayId;
import kh.edu.istad.stadoor.gateway.valueobject.service.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record RegisterServiceCommand(

        @TargetAggregateIdentifier
        ServiceId serviceId,

        GatewayId gatewayId,
        ServiceName name,
        ServiceType type,
        BaseUrl baseUrl,
        ServiceStatus status
) {
}
