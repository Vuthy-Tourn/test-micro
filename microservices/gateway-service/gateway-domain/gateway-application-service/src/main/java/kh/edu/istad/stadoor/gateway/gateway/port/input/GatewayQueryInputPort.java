package kh.edu.istad.stadoor.gateway.gateway.port.input;

import kh.edu.istad.stadoor.common.valueobject.TenantId;
import kh.edu.istad.stadoor.gateway.gateway.dto.query.DashboardOverviewResponse;
import kh.edu.istad.stadoor.gateway.gateway.dto.query.GatewayDetailResponse;
import kh.edu.istad.stadoor.gateway.gateway.dto.query.GatewaySummaryResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface GatewayQueryInputPort {

    Flux<GatewaySummaryResponse> getAllGateways();

    Mono<GatewayDetailResponse> getGatewayById(UUID gatewayId);

    Mono<GatewayDetailResponse> getGatewayByTenantIdAndGatewayName(UUID tenantId, String gatewayName);

    Flux<GatewaySummaryResponse> getAllGatewayByTenantId(UUID tenantId);

    Mono<DashboardOverviewResponse> getOverviewByTenantId(TenantId tenantId);
}
