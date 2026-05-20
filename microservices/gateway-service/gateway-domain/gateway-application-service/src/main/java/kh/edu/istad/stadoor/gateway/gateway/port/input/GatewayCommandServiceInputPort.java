package kh.edu.istad.stadoor.gateway.gateway.port.input;

import kh.edu.istad.stadoor.common.valueobject.TenantId;
import kh.edu.istad.stadoor.gateway.gateway.dto.create.CreateGatewayRequest;
import kh.edu.istad.stadoor.gateway.gateway.dto.create.CreateGatewayResponse;
import kh.edu.istad.stadoor.gateway.gateway.dto.create.GatewayCommandResponse;
import kh.edu.istad.stadoor.gateway.gateway.dto.update.ChangeStatusResponse;
import kh.edu.istad.stadoor.gateway.gateway.dto.update.UpdateGatewayRequest;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface GatewayCommandServiceInputPort {

    Mono<CreateGatewayResponse> createGateway(TenantId tenantId, CreateGatewayRequest createGatewayRequest);

    CompletableFuture<GatewayCommandResponse> updateGateway(UUID gatewayId, UpdateGatewayRequest updateGatewayRequest);

    Mono<ChangeStatusResponse> activateGateway(UUID gatewayId);

    Mono<ChangeStatusResponse> deactivateGateway(UUID gatewayId, UUID userId);

}
