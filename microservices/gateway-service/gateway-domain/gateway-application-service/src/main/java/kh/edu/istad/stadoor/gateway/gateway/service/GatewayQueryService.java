package kh.edu.istad.stadoor.gateway.gateway.service;

import kh.edu.istad.stadoor.common.valueobject.TenantId;
import kh.edu.istad.stadoor.gateway.gateway.dto.query.DashboardOverviewResponse;
import kh.edu.istad.stadoor.gateway.gateway.dto.query.GatewayDetailResponse;
import kh.edu.istad.stadoor.gateway.gateway.dto.query.GatewaySummaryResponse;
import kh.edu.istad.stadoor.gateway.gateway.port.input.GatewayQueryInputPort;
import kh.edu.istad.stadoor.gateway.gateway.port.output.GatewayQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GatewayQueryService implements GatewayQueryInputPort {

    private final GatewayQueryOutputPort gatewayQueryOutputPort;

    @Override
    public Flux<GatewaySummaryResponse> getAllGateways() {
        return gatewayQueryOutputPort.findAllGateways();
    }

    @Override
    public Mono<GatewayDetailResponse> getGatewayById(UUID gatewayId) {
        return gatewayQueryOutputPort.findGatewayById(gatewayId);
    }

    @Override
    public Mono<GatewayDetailResponse> getGatewayByTenantIdAndGatewayName(UUID tenantId, String gatewayName) {
        return gatewayQueryOutputPort.findGatewayByTenantIdAndGatewayName(tenantId, gatewayName);
    }


    @Override
    public Flux<GatewaySummaryResponse> getAllGatewayByTenantId(UUID tenantId) {
        return gatewayQueryOutputPort.findAllGatewaysByTenantId(tenantId);
    }

    // new feature support to frontend integration
    @Override
    public Mono<DashboardOverviewResponse> getOverviewByTenantId(TenantId tenantId) {
        return Mono.zip(
                gatewayQueryOutputPort.countGatewaysByTenantId(tenantId),
                gatewayQueryOutputPort.countServicesByTenantId(tenantId),
                gatewayQueryOutputPort.countRoutesByTenantId(tenantId)
        ).map(tuple -> new DashboardOverviewResponse(
                tuple.getT1(),
                tuple.getT2(),
                tuple.getT3()
        ));
    }


}
