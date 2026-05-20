package kh.edu.istad.stadoor.gateway.command.route;

import kh.edu.istad.stadoor.gateway.valueobject.route.HttpMethod;
import kh.edu.istad.stadoor.gateway.valueobject.route.RouteId;
import kh.edu.istad.stadoor.gateway.valueobject.route.RoutePath;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record UpdateRoutePathCommand(

        @TargetAggregateIdentifier
        RouteId routeId,
        RoutePath path,
        HttpMethod method
) {
}
