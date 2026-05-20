package kh.edu.istad.stadoor.gateway.route.ports.output;

import kh.edu.istad.stadoor.gateway.entity.Route;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RouteRepository {
//    Mono<Route> save(RouteCreatedEvent route);

    Mono<Route> findByRouteId(UUID routeId);
    Mono<Boolean> existsById(UUID id);
    Mono<Boolean> existsByRoutePathAndMethod(String path, String method);
    Mono<Boolean> existsByTargetPathAndMethod(String targetPath, String method);


    Flux<Route> findAll();
    Flux<Route> findByGatewayId(UUID gatewayId);
    Flux<Route> findByServiceID(UUID serviceId);

}
