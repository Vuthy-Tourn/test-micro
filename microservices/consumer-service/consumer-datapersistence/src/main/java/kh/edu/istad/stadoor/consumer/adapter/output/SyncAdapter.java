package kh.edu.istad.stadoor.consumer.adapter.output;

import kh.edu.istad.stadoor.consumer.port.output.SyncRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SyncAdapter implements SyncRepository {

    private final DatabaseClient databaseClient;

    @Override
    public Mono<Void> upsertTenant(UUID tenantId, String name) {
        return databaseClient.sql("""
                        INSERT INTO tenants (id, name, synced_at)
                        VALUES (:id, :name, :syncedAt)
                        ON CONFLICT (id) DO UPDATE SET name = :name, synced_at = :syncedAt
                        """)
                .bind("id", tenantId)
                .bind("name", name)
                .bind("syncedAt", Instant.now())
                .then();
    }

    @Override
    public Mono<Void> upsertGateway(UUID gatewayId, UUID tenantId, String name, String status, String authType) {
        return databaseClient.sql("""
                        INSERT INTO gateways (id, tenant_id, name, status, auth_type, synced_at)
                        VALUES (:id, :tenantId, :name, :status, :authType, :syncedAt)
                        ON CONFLICT (id) DO UPDATE
                            SET tenant_id = :tenantId, name = :name, status = :status,
                                auth_type = :authType, synced_at = :syncedAt
                        """)
                .bind("id", gatewayId)
                .bind("tenantId", tenantId)
                .bind("name", name)
                .bind("status", status)
                .bind("authType", authType)
                .bind("syncedAt", Instant.now())
                .then();
    }

    @Override
    public Mono<Void> updateGatewayStatus(UUID gatewayId, String status) {
        return databaseClient.sql("""
                        UPDATE gateways SET status = :status, synced_at = :syncedAt
                        WHERE id = :id
                        """)
                .bind("id", gatewayId)
                .bind("status", status)
                .bind("syncedAt", Instant.now())
                .then();
    }

    @Override
    public Mono<Void> updateGatewayName(UUID gatewayId, String name) {
        return databaseClient.sql("""
                        UPDATE gateways SET name = :name, synced_at = :syncedAt
                        WHERE id = :id
                        """)
                .bind("id", gatewayId)
                .bind("name", name)
                .bind("syncedAt", Instant.now())
                .then();
    }

    @Override
    public Mono<String> findGatewayAuthType(UUID gatewayId, UUID tenantId) {
        return databaseClient.sql("""
                        SELECT auth_type FROM gateways
                        WHERE id = :gatewayId AND tenant_id = :tenantId AND status = 'ACTIVE'
                        """)
                .bind("gatewayId", gatewayId)
                .bind("tenantId", tenantId)
                .map((row, meta) -> row.get("auth_type", String.class))
                .one();
    }
}
