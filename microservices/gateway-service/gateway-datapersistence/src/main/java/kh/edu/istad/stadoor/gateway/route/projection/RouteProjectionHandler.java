package kh.edu.istad.stadoor.gateway.route.projection;

import kh.edu.istad.stadoor.gateway.event.route.*;
import kh.edu.istad.stadoor.gateway.event.service.ServiceActivatedEvent;
import kh.edu.istad.stadoor.gateway.event.service.ServiceDeactivatedEvent;
import kh.edu.istad.stadoor.gateway.route.entity.RouteEntity;
import kh.edu.istad.stadoor.gateway.route.repository.RouteR2dbcRepository;
import kh.edu.istad.stadoor.gateway.valueobject.route.RouteStatus;
import kh.edu.istad.stadoor.gateway.valueobject.service.ServiceStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
//@ProcessingGroup("route-group")
@Component
public class RouteProjectionHandler {
    private final RouteR2dbcRepository routeR2dbcRepository;

    @EventHandler
    public void on (RouteCreatedEvent route) {

        RouteEntity routeEntity = new RouteEntity();
        routeEntity.setRouteId(route.routeId().routeId());
        routeEntity.setGatewayId(route.gatewayId().gatewayId());
        routeEntity.setServiceId(route.serviceId().serviceId());
        routeEntity.setRoutePath(route.path().routePath());
        routeEntity.setMethod(route.method().name());
        routeEntity.setStatus(route.status().name());
        routeEntity.setSecure(route.routeSecurity().name());
        routeEntity.setTargetPath(route.targetPath().targetPath());
        routeEntity.setCreatedAt(route.createdAt());
        routeEntity.setNew(routeEntity.getUpdatedAt() == null);

        routeR2dbcRepository.save(routeEntity)
                .doOnSuccess(saved -> System.out.println("Saved route: " + saved.getRouteId()))
                .doOnError(Throwable::printStackTrace)
                .subscribe();

    }

    @EventHandler
    public void on (RoutePathUpdatedEvent routePathUpdatedEvent){
        log.info("on Eventhandler {} ", routePathUpdatedEvent);

        routeR2dbcRepository.findById(routePathUpdatedEvent.routeId().routeId())
                .flatMap(route->{
                    route.setRoutePath(routePathUpdatedEvent.path().routePath());
                    route.setMethod(routePathUpdatedEvent.method().name());
                    route.setUpdatedAt(Instant.now());
                    route.setNew(route.getUpdatedAt() == null);
                    return routeR2dbcRepository.save(route);
                })
                .doOnSuccess(updated -> System.out.println("Updated route: " + updated.getRouteId()))
                .doOnError(Throwable::printStackTrace)
                .subscribe();
    }

    @EventHandler
    public void on (RouteTargetPathUpdatedEvent routeTargetPathUpdatedEvent){

        log.info("on Eventhandler {} ", routeTargetPathUpdatedEvent);

        routeR2dbcRepository.findById(routeTargetPathUpdatedEvent.routeId().routeId())
                .flatMap(route ->{
                    route.setTargetPath(routeTargetPathUpdatedEvent.targetPath().targetPath());
                    route.setUpdatedAt(Instant.now());
                    route.setNew(route.getUpdatedAt() == null);
                    return routeR2dbcRepository.save(route);
                })
                .doOnSuccess(updated -> System.out.println("Updated route: " + updated.getRouteId()))
                .doOnError(Throwable::printStackTrace)
                .subscribe();
    }

    @EventHandler
    public void on (RouteSecurityChangedEvent routeSecurityChangedEvent){

        log.info("on Eventhandler {} ", routeSecurityChangedEvent);

        routeR2dbcRepository.findById(routeSecurityChangedEvent.routeId().routeId())
                .flatMap(route -> {
                    route.setSecure(routeSecurityChangedEvent.routeSecurity().name());
                    route.setUpdatedAt(Instant.now());
                    route.setNew(route.getUpdatedAt() == null);
                    return routeR2dbcRepository.save(route);
                })
                .doOnSuccess(updated -> System.out.println("Updated route: " + updated.getRouteId()))
                .doOnError(Throwable::printStackTrace)
                .subscribe();
    }

    @EventHandler
    public void on(RouteActivatedEvent event) {
        updateStatus(event.routeId().routeId(), event.status());
    }


    @EventHandler
    public void on(RouteDeactivatedEvent event) {
        updateStatus(event.routeId().routeId(), event.status());
    }


    // Shared logic
    private void updateStatus(UUID routeId, RouteStatus status) {

        routeR2dbcRepository.findById(routeId)
                .flatMap(route -> {
                    route.setStatus(status.name());
                    route.setUpdatedAt(Instant.now());
                    route.setNew(route.getUpdatedAt() == null);
                    return routeR2dbcRepository.save(route);
                })
                .doOnSuccess(updated -> log.info("Route status updated: {}", updated))
                .doOnError(error -> log.error("Error updating status", error))
                .subscribe();
    }
}
