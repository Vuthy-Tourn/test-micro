package kh.edu.istad.stadoor.gateway.aggregate;

import kh.edu.istad.stadoor.gateway.command.route.*;
import kh.edu.istad.stadoor.gateway.event.route.*;
import kh.edu.istad.stadoor.common.valueobject.gateway.GatewayId;
import kh.edu.istad.stadoor.gateway.valueobject.route.*;
import kh.edu.istad.stadoor.gateway.valueobject.service.ServiceId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.time.Instant;

@Getter
@NoArgsConstructor
@Aggregate
@Slf4j
@EqualsAndHashCode
public class RouteAggregate {
    @AggregateIdentifier
    private RouteId routeId;

    private GatewayId gatewayId;
    private ServiceId serviceId;
    private RoutePath path;
    private HttpMethod method;
    private TargetPath targetPath;
    private RouteSecurity routeSecurity;
    private RouteStatus routeStatus;
    private Instant createdAt;
    private Instant updatedAt;

    @CommandHandler
    public RouteAggregate(CreateRouteCommand command) {
        log.info("Aggregate on createRouteCommand {}", command);

        RouteCreatedEvent routeCreatedEvent = RouteCreatedEvent.builder()
                .routeId(command.routeId())
                .gatewayId(command.gatewayId())
                .serviceId(command.serviceId())
                .path(command.path())
                .method(command.method())
                .targetPath(command.targetPath())
                .routeSecurity(command.routeSecurity())
                .status(RouteStatus.ACTIVE)
                .createdAt(Instant.now())
                .build();

        AggregateLifecycle.apply(routeCreatedEvent);
    }

    @CommandHandler
    public void handle (UpdateRoutePathCommand command) {
        log.info("Aggregate on updateRoutePathCommand {}", command);

        RoutePathUpdatedEvent routePathUpdatedEvent = RoutePathUpdatedEvent.builder()
                .routeId(command.routeId())
                .path(command.path())
                .method(command.method())
                .build();

        AggregateLifecycle.apply(routePathUpdatedEvent);
    }

    @CommandHandler
    public void handle (UpdateRouteTargetPathCommand command){
        log.info("Aggregate on updateRouteTargetPathCommand");

        RouteTargetPathUpdatedEvent routeTargetPathUpdatedEvent = RouteTargetPathUpdatedEvent.builder()
                .routeId(command.routeId())
                .targetPath(command.targetPath())
                .build();

        AggregateLifecycle.apply(routeTargetPathUpdatedEvent);
    }

    @CommandHandler
    public void handle (ChangeRouteSecurityCommand command){
        log.info("Aggregate on changeRouteSecurityCommand {}", command);

        RouteSecurityChangedEvent routeSecurityChangedEvent = RouteSecurityChangedEvent.builder()
                .routeId(command.routeId())
                .routeSecurity(command.routeSecurity())
                .build();

        AggregateLifecycle.apply(routeSecurityChangedEvent);
    }

    @CommandHandler
    public void handle (ActivateRouteCommand command){
        log.info("Aggregate on activateRouteCommand {}", command);

        RouteActivatedEvent routeActivatedEvent = RouteActivatedEvent.builder()
                .routeId(command.routeId())
                .status(RouteStatus.ACTIVE)
                .build();

        AggregateLifecycle.apply(routeActivatedEvent);
    }

    @CommandHandler
    public void handle (DeactivateRouteCommand command){
        log.info("Aggregate on deactivateRouteCommand {}", command);

        RouteDeactivatedEvent routeDeactivatedEvent = RouteDeactivatedEvent.builder()
                .routeId(command.routeId())
                .status(RouteStatus.INACTIVE)
                .build();

        AggregateLifecycle.apply(routeDeactivatedEvent);
    }

    @EventSourcingHandler
    public void on(RouteCreatedEvent event) {
        this.routeId = event.routeId();
        this.gatewayId = event.gatewayId();
        this.serviceId = event.serviceId();
        this.path = event.path();
        this.method = event.method();
        this.targetPath = event.targetPath();
        this.routeSecurity = event.routeSecurity();
        this.routeStatus = event.status();
        this.createdAt = event.createdAt();
    }

    @EventSourcingHandler
    public void on (RoutePathUpdatedEvent event) {
        this.routeId = event.routeId();
        this.path = event.path();
        this.method = event.method();
    }

    @EventSourcingHandler
    public void on (RouteTargetPathUpdatedEvent event) {
        this.routeId = event.routeId();
        this.targetPath = event.targetPath();
    }

    @EventSourcingHandler
    public void on (RouteSecurityChangedEvent event) {
        this.routeId = event.routeId();
        this.routeSecurity = event.routeSecurity();
    }

    @EventSourcingHandler
    public void on (RouteActivatedEvent event) {
        this.routeId = event.routeId();
        this.routeStatus = event.status();
    }

    @EventSourcingHandler
    public void on (RouteDeactivatedEvent event) {
        this.routeId = event.routeId();
        this.routeStatus = event.status();
    }
}
