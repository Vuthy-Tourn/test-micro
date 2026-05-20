package kh.edu.istad.stadoor.consumer.port.input;

import kh.edu.istad.stadoor.consumer.dto.request.LoginInput;
import kh.edu.istad.stadoor.consumer.dto.request.LogoutInput;
import kh.edu.istad.stadoor.consumer.dto.request.RefreshInput;
import kh.edu.istad.stadoor.consumer.dto.request.ValidateCredentialInput;
import kh.edu.istad.stadoor.consumer.dto.response.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ConsumerQueryPort {

    Mono<ConsumerResponse> findById(UUID id);

    Flux<ConsumerResponse> findByGatewayId(UUID gatewayId);

    Flux<ConsumerResponse> findByTenantId(UUID tenantId);

    Mono<ValidateCredentialResponse> validateCredential(ValidateCredentialInput input);

    Mono<LoginResponse> login(LoginInput loginInput);

    Mono<RefreshResponse> refresh(RefreshInput request);

    Mono<Void> logout(LogoutInput logoutInput);

    Mono<ConsumerAuthTypeCountResponse> countConsumersByAuthType();
}
