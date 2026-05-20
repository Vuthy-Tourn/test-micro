package kh.edu.istad.stadoor.gateway.controller.gateway;

import kh.edu.istad.stadoor.gateway.gateway.dto.create.CreateGatewayRequest;
import kh.edu.istad.stadoor.gateway.gateway.dto.create.CreateGatewayResponse;
import kh.edu.istad.stadoor.gateway.gateway.dto.create.GatewayCommandResponse;
import kh.edu.istad.stadoor.gateway.gateway.dto.update.ChangeStatusResponse;
import kh.edu.istad.stadoor.gateway.gateway.dto.update.UpdateGatewayRequest;
import kh.edu.istad.stadoor.gateway.gateway.port.input.GatewayCommandServiceInputPort;
import kh.edu.istad.stadoor.gateway.security.TenantIdJwtExtractor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/gateways")
@RequiredArgsConstructor
public class GatewayCommandController {

    private final GatewayCommandServiceInputPort gatewayCommandServiceInputPort;
    private final TenantIdJwtExtractor tenantIdJwtExtractor;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CreateGatewayResponse> createGateway(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateGatewayRequest request
    ) {
        return gatewayCommandServiceInputPort.createGateway(tenantIdJwtExtractor.extract(jwt), request);
    }

    @PutMapping("/{gatewayId}")
    public Mono<ResponseEntity<GatewayCommandResponse>> updateGateway(
            @PathVariable UUID gatewayId,
            @Valid @RequestBody UpdateGatewayRequest updateGatewayRequest
    ) {
        return Mono.fromFuture(gatewayCommandServiceInputPort.updateGateway(gatewayId, updateGatewayRequest))
                .map(response -> ResponseEntity.status(HttpStatus.ACCEPTED).body(response));
    }

//    @PatchMapping("/{gatewayId}/activate")
//    public Mono<ResponseEntity<ChangeStatusResponse>> activateGateway(@PathVariable UUID gatewayId) {
//        return Mono.fromFuture(gatewayCommandServiceInputPort.activateGateway(gatewayId))
//                .map(response -> ResponseEntity.status(HttpStatus.ACCEPTED).body(response));
//    }
//    @PatchMapping("/{gatewayId}/deactivate")
//    public Mono<ResponseEntity<ChangeStatusResponse>> deactivateGateway(
//            @AuthenticationPrincipal Jwt jwt,
//            @PathVariable UUID gatewayId) {
//        UUID userId = UUID.fromString(jwt.getSubject());
//        return Mono.fromFuture(gatewayCommandServiceInputPort.deactivateGateway(gatewayId, userId))
//                .map(response -> ResponseEntity.status(HttpStatus.ACCEPTED).body(response));
//    }

    @PatchMapping("/{gatewayId}/activate")
    public Mono<ResponseEntity<ChangeStatusResponse>> activateGateway(
            @PathVariable UUID gatewayId
    ) {
        return gatewayCommandServiceInputPort.activateGateway(gatewayId)
                .map(response -> ResponseEntity.status(HttpStatus.ACCEPTED).body(response));
    }

    @PatchMapping("/{gatewayId}/deactivate")
    public Mono<ResponseEntity<ChangeStatusResponse>> deactivateGateway(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID gatewayId
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return gatewayCommandServiceInputPort.deactivateGateway(gatewayId, userId)
                .map(response -> ResponseEntity.status(HttpStatus.ACCEPTED).body(response));
    }
}
