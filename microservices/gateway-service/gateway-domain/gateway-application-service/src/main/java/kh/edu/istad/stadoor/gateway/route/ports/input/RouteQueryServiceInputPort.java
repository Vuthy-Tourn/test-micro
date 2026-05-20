package kh.edu.istad.stadoor.gateway.route.ports.input;

import kh.edu.istad.stadoor.gateway.route.dto.RouteResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RouteQueryServiceInputPort {

    Flux<RouteResponse> getAllRoutes();
    Flux<RouteResponse> getRouteByGatewayId(UUID gatewayId);
    Flux<RouteResponse> getRoutesByServiceId(UUID serviceId);
    Mono<RouteResponse> getRouteById(UUID routeId);

}
