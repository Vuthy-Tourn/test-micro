package kh.edu.istad.stadoor.gateway.gateway.adapter;

import kh.edu.istad.stadoor.common.valueobject.TenantId;
import kh.edu.istad.stadoor.gateway.gateway.dto.query.DashboardOverviewResponse;
import kh.edu.istad.stadoor.gateway.gateway.dto.query.GatewayDetailResponse;
import kh.edu.istad.stadoor.gateway.gateway.dto.query.GatewaySummaryResponse;
import kh.edu.istad.stadoor.gateway.gateway.entity.GatewayEntity;
import kh.edu.istad.stadoor.gateway.gateway.port.output.GatewayQueryOutputPort;
import kh.edu.istad.stadoor.gateway.gateway.repository.GatewayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GatewayQueryPersistenceAdapter implements GatewayQueryOutputPort {
    private final DatabaseClient databaseClient;

    private final GatewayRepository gatewayRepository;

//    @Override
//    public Flux<GatewaySummaryResponse> findAllGateways() {
//        return gatewayRepository.findAll()
//                .map(this::toGatewaySummaryResponse);
//    }

    @Override
    public Flux<GatewaySummaryResponse> findAllGateways() {
        return databaseClient.sql("""
            SELECT
                g.gateway_id,
                g.tenant_id,
                g.gateway_name,
                g.description,
                g.gateway_type,
                g.auth_type,
                g.status,
                COUNT(DISTINCT s.service_id) AS total_services,
                COUNT(DISTINCT r.route_id)   AS total_routes,
                g.created_at,
                g.updated_at
            FROM gateways g
            LEFT JOIN services s ON s.gateway_id = g.gateway_id
            LEFT JOIN routes   r ON r.gateway_id = g.gateway_id
            GROUP BY
                g.gateway_id, g.tenant_id, g.gateway_name,
                g.description, g.gateway_type, g.auth_type,
                g.status, g.created_at, g.updated_at
            """)
                .map((row, metadata) -> toGatewaySummaryResponseFromRow(row))  // ← two args
                .all();
    }

    // ← Row, not Readable
    private GatewaySummaryResponse toGatewaySummaryResponseFromRow(io.r2dbc.spi.Row row) {
        LocalDateTime createdAt = row.get("created_at", LocalDateTime.class);
        LocalDateTime updatedAt = row.get("updated_at", LocalDateTime.class);

        return new GatewaySummaryResponse(
                row.get("gateway_id",     UUID.class),
                row.get("tenant_id",      UUID.class),
                row.get("gateway_name",   String.class),
                row.get("description",    String.class),
                row.get("gateway_type",   String.class),
                row.get("auth_type",      String.class),
                row.get("status",         String.class),
                row.get("total_services", Long.class),
                row.get("total_routes",   Long.class),
                createdAt != null ? createdAt.atZone(ZoneOffset.UTC) : null,
                updatedAt != null ? updatedAt.atZone(ZoneOffset.UTC) : null
        );
    }

    @Override
    public Mono<GatewayDetailResponse> findGatewayById(UUID gatewayId) {
        return gatewayRepository.findById(gatewayId)
                .map(this::toGatewayDetailResponse);
    }

    @Override
    public Mono<GatewayDetailResponse> findGatewayByTenantIdAndGatewayName(UUID tenantId, String gatewayName) {
        return gatewayRepository.findByTenantIdAndGatewayName(tenantId, gatewayName)
                .map(this::toGatewayDetailResponse);
    }

    // gateways belong to a tenant !
    @Override
    public Flux<GatewaySummaryResponse> findAllGatewaysByTenantId(UUID tenantId) {
        return gatewayRepository.findAllGatewaysByTenantId(tenantId)
                .map(this::toGatewaySummaryResponse);
    }

    // validate gateway already existed :
    @Override
    public Mono<Boolean> existsByTenantIdAndGatewayName(UUID tenantId, String gatewayName) {
        return gatewayRepository.existsByTenantIdAndGatewayName(tenantId, gatewayName);
    }


    @Override
    public Mono<Long> countGatewaysByTenantId(TenantId tenantId) {
        return databaseClient.sql("""
                SELECT COUNT(*) 
                FROM gateways 
                WHERE tenant_id = :tenantId
                """)
                .bind("tenantId", tenantId.id())
                .map(row -> row.get(0, Long.class))
                .one();
    }

    @Override
    public Mono<Long> countServicesByTenantId(TenantId tenantId) {
        return databaseClient.sql("""
                SELECT COUNT(*) 
                FROM services s
                JOIN gateways g ON s.gateway_id = g.gateway_id
                WHERE g.tenant_id = :tenantId
                """)
                .bind("tenantId", tenantId.id())
                .map(row -> row.get(0, Long.class))
                .one();
    }

    @Override
    public Mono<Long> countRoutesByTenantId(TenantId tenantId) {
        return databaseClient.sql("""
                SELECT COUNT(*) 
                FROM routes r
                JOIN gateways g ON r.gateway_id = g.gateway_id
                WHERE g.tenant_id = :tenantId
                """)
                .bind("tenantId", tenantId.id())
                .map(row -> row.get(0, Long.class))
                .one();
    }


    // for service package(sreypich validation)
//    @Override
//    public Mono<Boolean> existsByGatewayId(UUID gatewayId) {
//        return gatewayRepository.existsByGatewayId(gatewayId);
//    }




    private GatewaySummaryResponse toGatewaySummaryResponse(GatewayEntity entity) {
        return new GatewaySummaryResponse(
                entity.getGatewayId(),
                entity.getTenantId(),
                entity.getGatewayName(),
                entity.getDescription(),
                entity.getGatewayType(),
                entity.getAuthType(),
                entity.getStatus(),
                0L,
                0L,
                entity.getCreatedAt() != null ? entity.getCreatedAt().atZone(ZoneOffset.UTC) : null,
                entity.getUpdatedAt() != null ? entity.getUpdatedAt().atZone(ZoneOffset.UTC) : null
        );
    }

    private GatewayDetailResponse toGatewayDetailResponse(GatewayEntity entity) {
        return new GatewayDetailResponse(
                entity.getGatewayId(),
                entity.getTenantId(),
                entity.getGatewayName(),
                entity.getDescription(),
                entity.getGatewayType(),
                entity.getAuthType(),
                entity.getStatus(),
                entity.getCreatedAt() != null ? entity.getCreatedAt().atZone(ZoneOffset.UTC) : null,
                entity.getUpdatedAt() != null ? entity.getUpdatedAt().atZone(ZoneOffset.UTC) : null
        );
    }
}
