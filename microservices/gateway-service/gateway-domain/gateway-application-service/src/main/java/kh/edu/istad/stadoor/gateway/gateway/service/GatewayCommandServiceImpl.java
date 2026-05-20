package kh.edu.istad.stadoor.gateway.gateway.service;

import kh.edu.istad.stadoor.common.valueobject.gateway.GatewayStatus;
import kh.edu.istad.stadoor.gateway.command.gateway.ActivateGatewayCommand;
import kh.edu.istad.stadoor.gateway.command.gateway.DeactivateGatewayCommand;
import kh.edu.istad.stadoor.gateway.command.gateway.UpdateGatewayCommand;
import kh.edu.istad.stadoor.common.valueobject.TenantId;
import kh.edu.istad.stadoor.gateway.gateway.dto.create.CreateGatewayRequest;
import kh.edu.istad.stadoor.gateway.gateway.dto.create.CreateGatewayResponse;
import kh.edu.istad.stadoor.gateway.gateway.dto.create.GatewayCommandResponse;
import kh.edu.istad.stadoor.gateway.gateway.dto.update.ChangeStatusResponse;
import kh.edu.istad.stadoor.gateway.gateway.dto.update.UpdateGatewayRequest;
import kh.edu.istad.stadoor.gateway.gateway.exception.GatewayAlreadyExistsException;
import kh.edu.istad.stadoor.gateway.gateway.mapper.GatewayMapper;
import kh.edu.istad.stadoor.gateway.gateway.port.input.GatewayCommandServiceInputPort;
import kh.edu.istad.stadoor.gateway.gateway.port.output.GatewayQueryOutputPort;
import kh.edu.istad.stadoor.common.valueobject.gateway.GatewayId;
import kh.edu.istad.stadoor.gateway.gateway.ports.output.UserProfileOutputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class GatewayCommandServiceImpl implements GatewayCommandServiceInputPort {

    private final CommandGateway commandGateway;
    private final GatewayMapper gatewayMapper;
    private final GatewayQueryOutputPort gatewayQueryOutputPort;
    private final UserProfileOutputPort userProfileOutputPort;

    @Override
    public Mono<CreateGatewayResponse> createGateway(TenantId tenantId, CreateGatewayRequest request) {
        return gatewayQueryOutputPort
                .existsByTenantIdAndGatewayName(tenantId.id(), request.gatewayName())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new GatewayAlreadyExistsException(
                                "Gateway name already exists for this tenant"
                        ));
                    }

                    UUID gatewayUuid = UUID.randomUUID();
                    GatewayId gatewayId = new GatewayId(gatewayUuid);

                    return Mono.fromFuture(
                                    commandGateway.send(
                                            gatewayMapper.createGatewayRequestToCreateGatewayCommand(gatewayId, tenantId, request)
                                    )
                            )
                            .thenReturn(new CreateGatewayResponse(
                                    gatewayUuid,
                                    tenantId.id(),
                                    request.gatewayName(),
                                    request.description(),
                                    request.gatewayType().name(),
                                    request.authType().name(),
                                    request.status() != null ? request.status().name() : "ACTIVE",
                                    ZonedDateTime.now(),
                                    ZonedDateTime.now()
                            ));
                });
    }

    @Override
    public CompletableFuture<GatewayCommandResponse> updateGateway(UUID gatewayId, UpdateGatewayRequest updateGatewayRequest) {
        log.info("Application service on updating gateway request {}", updateGatewayRequest);

        UpdateGatewayCommand updateGatewayCommand =
                gatewayMapper.updateGatewayRequestToUpdateGatewayCommand(new GatewayId(gatewayId), updateGatewayRequest);

        return commandGateway.send(updateGatewayCommand)
                .thenApply(result -> new GatewayCommandResponse(gatewayId, "Gateway update command accepted."));
    }

//    @Override
//    public CompletableFuture<CreateGatewayResponse> activateGateway(UUID gatewayId) {
//        return commandGateway.send(new ActivateGatewayCommand(new GatewayId(gatewayId)))
//                .thenApply(result -> new CreateGatewayResponse(gatewayId, "Gateway activation command accepted."));
//    }
//
//    @Override
//    public CompletableFuture<CreateGatewayResponse> deactivateGateway(UUID gatewayId, UUID userId) {
//        return commandGateway.send(new DeactivateGatewayCommand(new GatewayId(gatewayId), userId))
//                .thenApply(result -> new CreateGatewayResponse(gatewayId, "Gateway deactivation command accepted."));
//    }

    @Override
    public Mono<ChangeStatusResponse> activateGateway(UUID gatewayId) {
        return Mono.fromCallable(() ->
                        commandGateway.sendAndWait(
                                new ActivateGatewayCommand(new GatewayId(gatewayId))
                        )
                )
                .subscribeOn(Schedulers.boundedElastic())
                .then(getOwnerEmailByGatewayId(gatewayId))
                .map(email -> new ChangeStatusResponse(
                        gatewayId,
                        "Gateway activation command accepted.",
                        GatewayStatus.ACTIVE,
                        email
                ));
    }

    @Override
    public Mono<ChangeStatusResponse> deactivateGateway(UUID gatewayId, UUID userId) {
        return Mono.fromCallable(() ->
                        commandGateway.sendAndWait(
                                new DeactivateGatewayCommand(new GatewayId(gatewayId), userId)
                        )
                )
                .subscribeOn(Schedulers.boundedElastic())
                .then(getOwnerEmailByGatewayId(gatewayId))
                .map(email -> new ChangeStatusResponse(
                        gatewayId,
                        "Gateway activation command accepted.",
                        GatewayStatus.ACTIVE,
                        email
                ));
    }

    private Mono<String> getOwnerEmailByGatewayId(UUID gatewayId) {
        return gatewayQueryOutputPort
                .findGatewayById(gatewayId)
                .switchIfEmpty(Mono.error(new RuntimeException(
                        "Gateway not found: " + gatewayId
                )))
                .flatMap(gateway ->
                        userProfileOutputPort
                                .getUserProfile(gateway.tenantId().toString())
                )
                .flatMap(page -> {
                    if (page.content() == null || page.content().isEmpty()) {
                        return Mono.error(new RuntimeException(
                                "No user found for tenantId of gateway: " + gatewayId
                        ));
                    }
                    return Mono.just(page.content().getFirst().email());
                });
    }
}
