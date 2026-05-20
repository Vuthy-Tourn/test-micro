package kh.edu.istad.stadoor.consumer.adapter.output;

import kh.edu.istad.stadoor.consumer.dto.request.SaveRefreshTokenRequest;
import kh.edu.istad.stadoor.consumer.dto.response.ConsumerAuthTypeCountResponse;
import kh.edu.istad.stadoor.consumer.dto.response.ConsumerResponse;
import kh.edu.istad.stadoor.consumer.entity.RefreshTokenEntity;
import kh.edu.istad.stadoor.consumer.mapper.ConsumerEntityMapper;
import kh.edu.istad.stadoor.consumer.port.output.ConsumerQueryRepository;
import kh.edu.istad.stadoor.consumer.projection.ConsumerCredentialProjection;
import kh.edu.istad.stadoor.consumer.projection.JwtCredentialProjection;
import kh.edu.istad.stadoor.consumer.projection.RefreshTokenProjection;
import kh.edu.istad.stadoor.consumer.repository.ConsumerReactiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ConsumerQueryAdapter implements ConsumerQueryRepository {

    private final ConsumerReactiveRepository consumerRepository;
    private final ConsumerEntityMapper mapper;
    private final DatabaseClient databaseClient;

    @Override
    public Mono<ConsumerResponse> findById(UUID id) {
        return consumerRepository.findById(id).map(mapper::toResponse);
    }

    @Override
    public Flux<ConsumerResponse> findByGatewayId(UUID gatewayId) {
        return consumerRepository.findByGatewayId(gatewayId).map(mapper::toResponse);
    }

    @Override
    public Flux<ConsumerResponse> findByTenantId(UUID tenantId) {
        return consumerRepository.findByTenantId(tenantId).map(mapper::toResponse);
    }

    @Override
    public Mono<ConsumerCredentialProjection> findBasicAuthByUsername(String username, UUID gatewayId) {
        return databaseClient.sql("""
                SELECT c.id AS consumer_id, c.tenant_id, c.gateway_id, b.password_hash AS credential_hash
                FROM consumers c
                JOIN consumer_credentials cc ON cc.consumer_id = c.id
                JOIN basic_auth_credentials b ON b.consumer_credential_id = cc.id
                WHERE b.username = :username AND c.gateway_id = :gatewayId AND cc.status = true AND c.status = 'ACTIVE'
                """)
                .bind("username", username)
                .bind("gatewayId", gatewayId)
                .map((row, meta) -> new ConsumerCredentialProjection(
                        row.get("consumer_id", UUID.class),
                        row.get("tenant_id", UUID.class),
                        row.get("gateway_id", UUID.class),
                        row.get("credential_hash", String.class),
                        null
                ))
                .one();
    }

    @Override
    public Mono<ConsumerCredentialProjection> findApiKeyByHash(String keyHash) {
        return databaseClient.sql("""
                SELECT c.id AS consumer_id, c.tenant_id, c.gateway_id
                FROM consumers c
                JOIN consumer_credentials cc ON cc.consumer_id = c.id
                JOIN api_key_credentials a ON a.consumer_credential_id = cc.id
                WHERE a.key_hash = :keyHash AND cc.status = true AND c.status = 'ACTIVE'
                """)
                .bind("keyHash", keyHash)
                .map((row, meta) -> new ConsumerCredentialProjection(
                        row.get("consumer_id", UUID.class),
                        row.get("tenant_id", UUID.class),
                        row.get("gateway_id", UUID.class),
                        null,
                        null
                ))
                .one();
    }

    @Override
    public Mono<ConsumerCredentialProjection> findJwtByConsumerId(UUID consumerId) {
        return databaseClient.sql("""
                SELECT c.id AS consumer_id, c.tenant_id, c.gateway_id, j.secret_key
                FROM consumers c
                JOIN consumer_credentials cc ON cc.consumer_id = c.id
                JOIN jwt_credentials j ON j.consumer_credential_id = cc.id
                WHERE c.id = :consumerId AND cc.status = true AND c.status = 'ACTIVE'
                """)
                .bind("consumerId", consumerId)
                .map((row, meta) -> new ConsumerCredentialProjection(
                        row.get("consumer_id", UUID.class),
                        row.get("tenant_id", UUID.class),
                        row.get("gateway_id", UUID.class),
                        null,
                        row.get("secret_key", String.class)
                ))
                .one();
    }

    @Override
    public Mono<JwtCredentialProjection> findJwtByUsername(String username, UUID gatewayId) {
        return databaseClient.sql("""
                SELECT c.id AS consumer_id, c.tenant_id, c.gateway_id, jc.password_hash AS credential_hash, 
                       jc.id AS jwt_credential_id
                FROM consumers c
                JOIN consumer_credentials cc ON cc.consumer_id = c.id
                JOIN jwt_credentials jc ON jc.consumer_credential_id = cc.id
                WHERE jc.username = :username AND jc.gateway_id = :gatewayId AND cc.status = true AND c.status = 'ACTIVE' 
                """)
                .bind("username", username)
                .bind("gatewayId", gatewayId)
                .map((row, meta) -> new JwtCredentialProjection(
                        row.get("consumer_id", UUID.class),
                        row.get("tenant_id", UUID.class),
                        row.get("gateway_id", UUID.class),
                        row.get("credential_hash", String.class),
                        row.get("jwt_credential_id", UUID.class)
                ))
                .one();
    }

    @Override
    public Mono<Void> saveRefreshToken(SaveRefreshTokenRequest saveRefreshTokenRequest) {
        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
        refreshTokenEntity.setId(saveRefreshTokenRequest.id());
        refreshTokenEntity.setJwtCredentialId(saveRefreshTokenRequest.jwtCredentialId());
        refreshTokenEntity.setTokenHash(saveRefreshTokenRequest.tokenHash());
        refreshTokenEntity.setRevoked(false);
        refreshTokenEntity.setExpiresAt(saveRefreshTokenRequest.expiresAt());
        refreshTokenEntity.setCreatedAt(saveRefreshTokenRequest.createdAt());
        return databaseClient.sql("""                                                                         
                INSERT INTO refresh_tokens (id, jwt_credential_id, token_hash, expires_at, revoked, created_at)                                                                        
                VALUES (:id, :jwtCredentialId, :tokenHash, :expiresAt, false, :createdAt)
                """)
                .bind("id", saveRefreshTokenRequest.id())
                .bind("jwtCredentialId", saveRefreshTokenRequest.jwtCredentialId())
                .bind("tokenHash", saveRefreshTokenRequest.tokenHash())
                .bind("expiresAt", saveRefreshTokenRequest.expiresAt())
                .bind("createdAt", saveRefreshTokenRequest.createdAt())
                .then();
    }

    @Override
    public Mono<RefreshTokenProjection> findConsumerInfoByTokenHash(String tokenHash) {
        return databaseClient.sql("""
                SELECT c.id AS consumer_id, c.tenant_id, c.gateway_id, jc.username, jc.id AS jwt_credential_id
                FROM refresh_tokens rf
                JOIN jwt_credentials jc ON jc.id = rf.jwt_credential_id
                JOIN consumer_credentials cc ON cc.id = jc.consumer_credential_id
                JOIN consumers c ON c.id = cc.consumer_id
                WHERE rf.token_hash = :tokenHash AND rf.revoked = false AND rf.expires_at > NOW() AND cc.status = true 
                AND c.status = 'ACTIVE'
                """)
                .bind("tokenHash", tokenHash)
                .map((row, meta) -> new RefreshTokenProjection(
                        row.get("consumer_id", UUID.class),
                        row.get("tenant_id", UUID.class),
                        row.get("gateway_id", UUID.class),
                        row.get("username", String.class),
                        row.get("jwt_credential_id", UUID.class)
                ))
                .one();
    }

    @Override
    public Mono<Void> revokeRefreshToken(String tokenHash) {
        return databaseClient.sql("""
                UPDATE refresh_tokens
                SET revoked = true
                WHERE token_hash = :tokenHash
                """)
                .bind("tokenHash", tokenHash)
                .then();
    }

    @Override
    public Flux<String> findRoleNamesByConsumerId(UUID consumerId) {
        return databaseClient.sql("""
                SELECT cr.name
                FROM consumer_roles cr
                JOIN consumer_role_mappings crm ON crm.role_id = cr.id
                WHERE crm.consumer_id = :consumerId
                """)
                .bind("consumerId", consumerId)
                .map(row  -> row.get("name", String.class))
                .all();
    }

    @Override
    public Mono<ConsumerAuthTypeCountResponse> countConsumersByAuthType() {
        return databaseClient.sql("""
                SELECT auth_type, COUNT(*) AS count
                FROM consumers
                GROUP BY auth_type
                """)
                .map((row, meta) -> Map.entry(
                        row.get("auth_type", String.class),
                        row.get("count", Long.class)
                ))
                .all()
                .collectMap(Map.Entry::getKey, Map.Entry::getValue)
                .map(byAuthType -> {
                    long total = byAuthType.values().stream()
                            .mapToLong(Long::longValue)
                            .sum();
                    return new ConsumerAuthTypeCountResponse(total, byAuthType);
                });
    }
}
