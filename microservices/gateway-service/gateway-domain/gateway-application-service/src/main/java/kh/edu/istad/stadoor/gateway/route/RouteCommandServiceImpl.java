package kh.edu.istad.stadoor.gateway.route;

import kh.edu.istad.stadoor.gateway.command.route.*;
import kh.edu.istad.stadoor.gateway.route.dto.CreateRouteRequest;
import kh.edu.istad.stadoor.gateway.route.dto.CreateRouteResponse;
import kh.edu.istad.stadoor.gateway.route.dto.update.UpdateRouteRequest;
import kh.edu.istad.stadoor.gateway.route.dto.update.UpdateRouteResponse;
import kh.edu.istad.stadoor.gateway.route.exception.RouteAlreadyExistsException;
import kh.edu.istad.stadoor.gateway.route.exception.RouteNotFoundException;
import kh.edu.istad.stadoor.gateway.route.mapper.RouteMapper;
import kh.edu.istad.stadoor.gateway.route.ports.input.RouteCommandServiceInputPort;
import kh.edu.istad.stadoor.gateway.route.ports.output.RouteRepository;
import kh.edu.istad.stadoor.gateway.service.ports.output.ServiceRepository;
import kh.edu.istad.stadoor.gateway.valueobject.route.RouteId;
import kh.edu.istad.stadoor.gateway.valueobject.route.RoutePath;
import kh.edu.istad.stadoor.gateway.valueobject.route.RouteStatus;
import kh.edu.istad.stadoor.gateway.valueobject.route.TargetPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Component
public class RouteCommandServiceImpl implements RouteCommandServiceInputPort {

    public final CommandGateway commandGateway;
    public final RouteMapper routeMapper;
    public final ServiceRepository serviceRepository;
    public final RouteRepository routeRepository;

    @Override
    public Mono<CreateRouteResponse> createRoute(CreateRouteRequest createRouteRequest) {

        return serviceRepository.existsById(createRouteRequest.serviceId())
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new RouteNotFoundException("Service Not found"));
                    }

                    return Mono.zip(
                            routeRepository.existsByRoutePathAndMethod(
                                    createRouteRequest.path(),
                                    createRouteRequest.method().name()
                            ),
                            routeRepository.existsByTargetPathAndMethod(
                                    createRouteRequest.targetPath(),
                                    createRouteRequest.method().name()
                            )
                    );
                })
                .flatMap(tuple -> {
                    boolean pathExists = tuple.getT1();
                    boolean targetExists = tuple.getT2();

                    if (pathExists) {
                        return Mono.error(
                                new RouteAlreadyExistsException("Route path already exists for this method")
                        );
                    }

                    if (targetExists) {
                        return Mono.error(
                                new RouteAlreadyExistsException("Target path already exists for this method")
                        );
                    }

                    RouteId routeId = new RouteId(UUID.randomUUID());

                    CreateRouteCommand command =
                            routeMapper.createRouteRequestToCreateRouteCommand(
                                    routeId,
                                    createRouteRequest
                            );

                    return Mono.fromFuture(commandGateway.send(command))
                            .thenReturn(
                                    CreateRouteResponse.builder()
                                            .routeId(routeId.routeId())
                                            .serviceId(command.serviceId().serviceId())
                                            .gatewayId(command.gatewayId().gatewayId())
                                            .path(command.path().routePath())
                                            .method(command.method())
                                            .routeSecurity(command.routeSecurity())
                                            .targetPath(command.targetPath().targetPath())
                                            .message("Create route successfully")
                                            .build()
                            );
                });
    }

    @Override
    public Mono<UpdateRouteResponse> updateRoute(UUID routeId, UpdateRouteRequest request) {

        return routeRepository.existsById(routeId)
                .flatMap(exist -> {
                    if (!exist) {
                        return Mono.error(new RouteNotFoundException("Route Not found"));
                    }
                    return routeRepository.findByRouteId(routeId);
                })
                .flatMap(currentRoute -> {

                    RouteId route = new RouteId(routeId);

                    Mono<Void> updatePath = Mono.empty();
                    Mono<Void> updateTarget = Mono.empty();
                    Mono<Void> changeSecurity = Mono.empty();

                    // 1. Update Path
                    if (!currentRoute.getRoutePath().routePath().equals(request.path())) {

                        updatePath = routeRepository
                                .existsByRoutePathAndMethod(request.path(), request.method().name())
                                .flatMap(exists -> {
                                    if (exists) {
                                        return Mono.error(new RouteAlreadyExistsException(
                                                "Route path already exists for this method"
                                        ));
                                    }

                                    UpdateRoutePathCommand command =
                                            new UpdateRoutePathCommand(
                                                    route,
                                                    new RoutePath(request.path()),
                                                    request.method()
                                            );

                                    return Mono.fromFuture(commandGateway.send(command)).then();
                                });
                    }

                    // 2. Update Target Path
                    if (!currentRoute.getTargetPath().targetPath().equals(request.targetPath())) {

                        updateTarget = routeRepository
                                .existsByTargetPathAndMethod(request.targetPath(), request.method().name())
                                .flatMap(exists -> {
                                    if (exists) {
                                        return Mono.error(new RouteAlreadyExistsException(
                                                "Target path already exists for this method"
                                        ));
                                    }

                                    UpdateRouteTargetPathCommand command =
                                            new UpdateRouteTargetPathCommand(
                                                    route,
                                                    new TargetPath(request.targetPath())
                                            );

                                    return Mono.fromFuture(commandGateway.send(command)).then();
                                });
                    }

                    // 3. Change Security
                    if (!currentRoute.getSecure().equals(request.routeSecurity())) {

                        ChangeRouteSecurityCommand command =
                                new ChangeRouteSecurityCommand(route, request.routeSecurity());

                        changeSecurity = Mono.fromFuture(commandGateway.send(command)).then();
                    }

                    // Combine all operations
                    return Mono.when(updatePath, updateTarget, changeSecurity)
                            .thenReturn(
                                    UpdateRouteResponse.builder()
                                            .routeId(routeId)
                                            .path(request.path())
                                            .targetPath(request.targetPath())
                                            .method(request.method())
                                            .routeSecurity(request.routeSecurity())
                                            .message("Route updated successfully")
                                            .build()
                            );
                });
    }

    @Override
    public Mono<String> activateRoute(UUID routeId) {
        return routeRepository.findByRouteId(routeId)
                .flatMap(route -> {
                    if (route.getStatus().equals(RouteStatus.ACTIVE)) {
                        return Mono.error(new IllegalStateException("Route already active"));
                    }

                    ActivateRouteCommand command =
                            new ActivateRouteCommand(new RouteId(routeId));

                    return Mono.fromFuture(commandGateway.send(command))
                            .thenReturn("Route activated");
                });
    }

    @Override
    public Mono<String> deactivateRoute(UUID routeId) {
        return routeRepository.findByRouteId(routeId)
                .flatMap(route -> {
                    if (route.getStatus().equals(RouteStatus.INACTIVE)) {
                        return Mono.error(new IllegalStateException("Route already deactivated"));
                    }

                    DeactivateRouteCommand command =
                            new DeactivateRouteCommand(new RouteId(routeId));

                    return Mono.fromFuture(commandGateway.send(command))
                            .thenReturn("Route deactivated");
                });
    }

}
