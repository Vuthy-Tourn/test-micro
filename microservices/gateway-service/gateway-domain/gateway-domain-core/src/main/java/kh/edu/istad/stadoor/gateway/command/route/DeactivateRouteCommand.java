package kh.edu.istad.stadoor.gateway.command.route;

import kh.edu.istad.stadoor.gateway.valueobject.route.RouteId;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record DeactivateRouteCommand(
        @TargetAggregateIdentifier
        RouteId routeId
) {
}
