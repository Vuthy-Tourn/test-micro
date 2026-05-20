
package kh.edu.istad.stadoor.gateway.command.route;

import kh.edu.istad.stadoor.gateway.valueobject.route.RouteId;
import kh.edu.istad.stadoor.gateway.valueobject.route.RouteSecurity;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record ChangeRouteSecurityCommand(

        @TargetAggregateIdentifier
        RouteId routeId,
        RouteSecurity routeSecurity
) {
}
