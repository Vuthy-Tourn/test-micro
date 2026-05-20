

package kh.edu.istad.stadoor.gateway.command.route;

import kh.edu.istad.stadoor.gateway.valueobject.route.RouteId;
import kh.edu.istad.stadoor.gateway.valueobject.route.TargetPath;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record UpdateRouteTargetPathCommand(
        @TargetAggregateIdentifier
        RouteId routeId,
        TargetPath targetPath
) {
}
