package kh.edu.istad.stadoor.gateway.route.repository;

import kh.edu.istad.stadoor.gateway.route.entity.RouteEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;


public interface RouteR2dbcRepository extends R2dbcRepository<RouteEntity, UUID> {


    Mono<Boolean> existsByRoutePathAndMethod(String path, String method);
    Mono<Boolean> existsByTargetPathAndMethod(String targetPath, String method);
    Flux<RouteEntity> findByGatewayId(UUID gatewayId);
    Flux<RouteEntity> findByServiceId(UUID serviceId);
}
