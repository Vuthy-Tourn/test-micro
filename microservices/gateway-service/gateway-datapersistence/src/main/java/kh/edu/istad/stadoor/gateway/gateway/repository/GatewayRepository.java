package kh.edu.istad.stadoor.gateway.gateway.repository;

import kh.edu.istad.stadoor.gateway.gateway.entity.GatewayEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface GatewayRepository extends R2dbcRepository<GatewayEntity, UUID> {
    Flux<GatewayEntity> findAllGatewaysByTenantId(UUID tenantId);
    Mono<GatewayEntity> findByTenantIdAndGatewayName(UUID tenantId, String gatewayName);
    Mono<Boolean> existsByTenantIdAndGatewayName(UUID tenantId, String gatewayName);

    Mono<Boolean> existsByGatewayId(UUID gatewayId);
    @Query("SELECT gateway_name FROM gateways WHERE gateway_id = :gatewayId")
    Mono<String> findGatewayNameById(UUID gatewayId);
}
