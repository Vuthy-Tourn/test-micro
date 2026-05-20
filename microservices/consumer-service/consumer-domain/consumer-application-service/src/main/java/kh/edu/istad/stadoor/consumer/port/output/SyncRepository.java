package kh.edu.istad.stadoor.consumer.port.output;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SyncRepository {

    Mono<Void> upsertTenant(UUID tenantId, String name);

    Mono<Void> upsertGateway(UUID gatewayId, UUID tenantId, String name, String status, String authType);

    Mono<Void> updateGatewayStatus(UUID gatewayId, String status);

    Mono<Void> updateGatewayName(UUID gatewayId, String name);

    Mono<String> findGatewayAuthType(UUID gatewayId, UUID tenantId);
}
