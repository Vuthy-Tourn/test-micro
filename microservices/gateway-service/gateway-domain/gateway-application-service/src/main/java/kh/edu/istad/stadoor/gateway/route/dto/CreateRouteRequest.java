package kh.edu.istad.stadoor.gateway.route.dto;

import kh.edu.istad.stadoor.gateway.valueobject.route.*;

import java.util.UUID;

public record CreateRouteRequest(
        UUID gatewayId,
        UUID serviceId,
        String path,
        HttpMethod method,
        String targetPath,
        RouteSecurity routeSecurity
) {}
