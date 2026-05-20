package kh.edu.istad.stadoor.consumer.controller;

import jakarta.validation.Valid;
import kh.edu.istad.stadoor.consumer.dto.request.LoginRequest;
import kh.edu.istad.stadoor.consumer.dto.request.LogoutRequest;
import kh.edu.istad.stadoor.consumer.dto.request.RefreshRequest;
import kh.edu.istad.stadoor.consumer.dto.request.ValidateCredentialRequest;
import kh.edu.istad.stadoor.consumer.dto.response.*;
import kh.edu.istad.stadoor.consumer.mapper.ConsumerRequestMapper;
import kh.edu.istad.stadoor.consumer.port.input.ConsumerQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/consumers")
@RequiredArgsConstructor
@Slf4j
public class ConsumerQueryController {

    private final ConsumerQueryPort consumerQueryPort;
    private final ConsumerRequestMapper mapper;

    @GetMapping("/{id}")
    public Mono<ApiResponse<ConsumerResponse>> findById(@PathVariable UUID id) {
        return consumerQueryPort.findById(id)
                .map(r -> ApiResponse.ok("Consumer found", r));
    }

    @GetMapping("/gateway/{gatewayId}")
    public Mono<ApiResponse<List<ConsumerResponse>>> findByGatewayId(@PathVariable UUID gatewayId) {
        return consumerQueryPort.findByGatewayId(gatewayId)
                .collectList()
                .map(r -> ApiResponse.ok("Consumers found", r));
    }

    @GetMapping("/tenant/{tenantId}")
    public Mono<ApiResponse<List<ConsumerResponse>>> findByTenantId(@PathVariable UUID tenantId) {
        return consumerQueryPort.findByTenantId(tenantId)
                .collectList()
                .map(r -> ApiResponse.ok("Consumer found", r));
    }

    @PostMapping("/validate")
    public Mono<ApiResponse<ValidateCredentialResponse>> validateCredential(
            @Valid @RequestBody ValidateCredentialRequest request) {
        return consumerQueryPort.validateCredential(mapper.toInput(request))
                .map(r -> ApiResponse.ok("Credential validated", r));
    }

    @PostMapping("/login")
    public Mono<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        return consumerQueryPort.login(mapper.toInput(request))
                .map(r -> ApiResponse.ok("Login successfully", r));
    }

    @PostMapping("/refresh")
    public Mono<ApiResponse<RefreshResponse>> refresh(@Valid @RequestBody RefreshRequest request) {
        return consumerQueryPort.refresh(mapper.toInput(request))
                .map(r -> ApiResponse.ok("Refresh token successfully", r));
    }

    @PostMapping("/logout")
    public Mono<ApiResponse<Void>> logout(@Valid @RequestBody LogoutRequest logoutRequest) {
        return consumerQueryPort.logout(mapper.toInput(logoutRequest))
                .thenReturn(ApiResponse.ok("Logout successfully", null));
    }

    @GetMapping("/admin/auth-type-count")
    public Mono<ApiResponse<ConsumerAuthTypeCountResponse>> countConsumersByAuthType() {
        return consumerQueryPort.countConsumersByAuthType()
                .map(r -> ApiResponse.ok("Consumer auth type counts found", r));
    }
}
