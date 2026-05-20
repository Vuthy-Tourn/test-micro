package kh.edu.istad.stadoor.gateway.command.route;

import kh.edu.istad.stadoor.common.valueobject.gateway.GatewayId;
import kh.edu.istad.stadoor.gateway.valueobject.route.*;
import kh.edu.istad.stadoor.gateway.valueobject.service.ServiceId;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record CreateRouteCommand(

        @TargetAggregateIdentifier
        RouteId routeId,
        GatewayId gatewayId,
        ServiceId serviceId,
        RoutePath path,
        HttpMethod method,
        TargetPath targetPath,
        RouteSecurity routeSecurity
) {
}
