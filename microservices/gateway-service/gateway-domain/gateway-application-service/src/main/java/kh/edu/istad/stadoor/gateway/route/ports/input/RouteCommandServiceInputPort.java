package kh.edu.istad.stadoor.gateway.route.ports.input;

import kh.edu.istad.stadoor.gateway.route.dto.CreateRouteRequest;
import kh.edu.istad.stadoor.gateway.route.dto.CreateRouteResponse;
import kh.edu.istad.stadoor.gateway.route.dto.update.UpdateRouteRequest;
import kh.edu.istad.stadoor.gateway.route.dto.update.UpdateRouteResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RouteCommandServiceInputPort {

    Mono<CreateRouteResponse> createRoute(CreateRouteRequest createRouteRequest);
    Mono<UpdateRouteResponse> updateRoute(UUID routeId, UpdateRouteRequest updateRouteRequest);
    Mono<String> activateRoute(UUID routeId);
    Mono<String> deactivateRoute(UUID routeId);
}
