package kh.edu.istad.stadoor.consumer.port.output;

import kh.edu.istad.stadoor.consumer.dto.request.SaveRefreshTokenRequest;
import kh.edu.istad.stadoor.consumer.dto.response.ConsumerAuthTypeCountResponse;
import kh.edu.istad.stadoor.consumer.dto.response.ConsumerResponse;
import kh.edu.istad.stadoor.consumer.projection.ConsumerCredentialProjection;
import kh.edu.istad.stadoor.consumer.projection.JwtCredentialProjection;
import kh.edu.istad.stadoor.consumer.projection.RefreshTokenProjection;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ConsumerQueryRepository {

    Mono<ConsumerResponse> findById(UUID id);

    Flux<ConsumerResponse> findByGatewayId(UUID gatewayId);

    Flux<ConsumerResponse> findByTenantId(UUID tenantId);

    Mono<ConsumerCredentialProjection> findBasicAuthByUsername(String username, UUID gatewayId);

    Mono<ConsumerCredentialProjection> findApiKeyByHash(String keyHash);

    Mono<ConsumerCredentialProjection> findJwtByConsumerId(UUID consumerId);

    Mono<JwtCredentialProjection> findJwtByUsername(String username, UUID gatewayId);

    Mono<Void> saveRefreshToken(SaveRefreshTokenRequest saveRefreshTokenRequest);

    Mono<RefreshTokenProjection> findConsumerInfoByTokenHash(String tokenHash);

    Mono<Void> revokeRefreshToken(String tokenHash);

    Flux<String> findRoleNamesByConsumerId(UUID consumerId);

    Mono<ConsumerAuthTypeCountResponse> countConsumersByAuthType();
}
