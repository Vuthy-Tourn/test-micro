package kh.edu.istad.stadoor.gateway.gateway.port.output;

import kh.edu.istad.stadoor.common.valueobject.TenantId;
import kh.edu.istad.stadoor.gateway.gateway.dto.query.DashboardOverviewResponse;
import kh.edu.istad.stadoor.gateway.gateway.dto.query.GatewayDetailResponse;
import kh.edu.istad.stadoor.gateway.gateway.dto.query.GatewaySummaryResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface GatewayQueryOutputPort {

    // for admin stadoor
    Flux<GatewaySummaryResponse> findAllGateways();

    Mono<GatewayDetailResponse> findGatewayById(UUID gatewayId);

    Mono<GatewayDetailResponse> findGatewayByTenantIdAndGatewayName(UUID tenantId, String gatewayName);

    Flux<GatewaySummaryResponse> findAllGatewaysByTenantId(UUID tenantId);

    // validate Existed gateway name By tenantId
    Mono<Boolean> existsByTenantIdAndGatewayName(UUID tenantId, String gatewayName);

//    Mono<Boolean> existsByGatewayId(UUID gatewayId);

    Mono<Long> countGatewaysByTenantId(TenantId tenantId);
    Mono<Long> countServicesByTenantId(TenantId tenantId);
    Mono<Long> countRoutesByTenantId(TenantId tenantId);

}
