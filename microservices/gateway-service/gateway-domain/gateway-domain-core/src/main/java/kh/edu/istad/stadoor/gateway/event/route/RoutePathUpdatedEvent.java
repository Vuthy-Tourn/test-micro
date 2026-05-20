package kh.edu.istad.stadoor.gateway.event.route;

import kh.edu.istad.stadoor.gateway.valueobject.route.HttpMethod;
import kh.edu.istad.stadoor.gateway.valueobject.route.RouteId;
import kh.edu.istad.stadoor.gateway.valueobject.route.RoutePath;
import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
public record RoutePathUpdatedEvent(
        RouteId routeId,
        RoutePath path,
        HttpMethod method,
        ZonedDateTime updatedAt
) {
}