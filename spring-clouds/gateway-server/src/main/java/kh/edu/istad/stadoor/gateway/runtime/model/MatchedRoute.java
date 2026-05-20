package kh.edu.istad.stadoor.gateway.runtime.model;

public record MatchedRoute(
        GatewayRuntimeConfig gateway,
        ServiceRuntimeConfig service,
        RouteRuntimeConfig route
) {
}
