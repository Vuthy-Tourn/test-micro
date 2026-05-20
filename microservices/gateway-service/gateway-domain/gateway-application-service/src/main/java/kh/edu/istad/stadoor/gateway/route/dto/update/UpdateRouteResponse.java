package kh.edu.istad.stadoor.gateway.route.dto.update;

import kh.edu.istad.stadoor.gateway.valueobject.route.HttpMethod;
import kh.edu.istad.stadoor.gateway.valueobject.route.RouteSecurity;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UpdateRouteResponse(
        UUID routeId,
        String path,
        HttpMethod method,
        String targetPath,
        RouteSecurity routeSecurity,
        String message
) {
}
