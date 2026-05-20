package kh.edu.istad.stadoor.gateway.route.adapter;

import kh.edu.istad.stadoor.gateway.entity.Route;
import kh.edu.istad.stadoor.gateway.route.exception.RouteNotFoundException;
import kh.edu.istad.stadoor.gateway.route.mapper.RouteEntityMapper;
import kh.edu.istad.stadoor.gateway.route.ports.output.RouteRepository;
import kh.edu.istad.stadoor.gateway.route.repository.RouteR2dbcRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class RouteRepositoryOutputPortAdapter implements RouteRepository {

    private final RouteR2dbcRepository routeR2dbcRepository;
    private final RouteEntityMapper routeEntityMapper;


    @Override
    public Mono<Route> findByRouteId(UUID routeId) {
        return routeR2dbcRepository.findById(routeId)
                .switchIfEmpty(Mono.error(new RouteNotFoundException("Route not found")))
                .map(routeEntityMapper::RouteEntityToRoute);
    }

    @Override
    public Mono<Boolean> existsById(UUID id) {
        return routeR2dbcRepository.existsById(id);
    }

    @Override
    public Mono<Boolean> existsByRoutePathAndMethod(String path, String method) {
        return routeR2dbcRepository.existsByRoutePathAndMethod(path, method);
    }

    @Override
    public Mono<Boolean> existsByTargetPathAndMethod(String targetPath, String method) {
        return routeR2dbcRepository.existsByTargetPathAndMethod(targetPath, method);
    }


    @Override
    public Flux<Route> findAll() {
        return routeR2dbcRepository.findAll()
                .map(routeEntityMapper::RouteEntityToRoute);
    }

    @Override
    public Flux<Route> findByGatewayId(UUID gatewayId) {
        return routeR2dbcRepository.findByGatewayId(gatewayId)
                .map(routeEntityMapper::RouteEntityToRoute);
    }

    @Override
    public Flux<Route> findByServiceID(UUID serviceId) {
        return routeR2dbcRepository.findByServiceId(serviceId)
                .map(routeEntityMapper::RouteEntityToRoute);
    }

}
