package kh.edu.istad.stadoor.gateway.event.route;
import kh.edu.istad.stadoor.gateway.valueobject.route.RouteId;
import kh.edu.istad.stadoor.gateway.valueobject.route.TargetPath;
import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
public record RouteTargetPathUpdatedEvent(
        RouteId routeId,
        TargetPath targetPath,
        ZonedDateTime updatedAt
) {
}