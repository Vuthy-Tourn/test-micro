
package kh.edu.istad.stadoor.gateway.event.route;

import kh.edu.istad.stadoor.gateway.valueobject.route.RouteId;
import kh.edu.istad.stadoor.gateway.valueobject.route.RouteStatus;
import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
public record RouteActivatedEvent(
        RouteId routeId,
        RouteStatus status,
        ZonedDateTime updatedAt
) {
}