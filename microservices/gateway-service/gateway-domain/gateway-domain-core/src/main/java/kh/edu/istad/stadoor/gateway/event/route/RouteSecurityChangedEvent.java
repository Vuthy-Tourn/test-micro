
package kh.edu.istad.stadoor.gateway.event.route;
import kh.edu.istad.stadoor.gateway.valueobject.route.RouteId;
import kh.edu.istad.stadoor.gateway.valueobject.route.RouteSecurity;
import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
public record RouteSecurityChangedEvent(
        RouteId routeId,
        RouteSecurity routeSecurity,
        ZonedDateTime updatedAt
) {
}