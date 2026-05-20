package kh.edu.istad.stadoor.gateway.runtime.service;

import kh.edu.istad.stadoor.gateway.gateway.port.input.GatewayQueryInputPort;
import kh.edu.istad.stadoor.gateway.gateway.dto.query.GatewayDetailResponse;
import kh.edu.istad.stadoor.gateway.route.dto.RouteResponse;
import kh.edu.istad.stadoor.gateway.route.ports.input.RouteQueryServiceInputPort;
import kh.edu.istad.stadoor.gateway.runtime.dto.GatewayRuntimeResponse;
import kh.edu.istad.stadoor.gateway.runtime.dto.RouteRuntimeItem;
import kh.edu.istad.stadoor.gateway.runtime.dto.ServiceRuntimeItem;
import kh.edu.istad.stadoor.gateway.runtime.port.input.RuntimeQueryInputPort;
import kh.edu.istad.stadoor.gateway.service.dto.ServiceResponse;
import kh.edu.istad.stadoor.gateway.service.ports.input.ServiceQueryServiceInputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RuntimeQueryService implements RuntimeQueryInputPort {

    private final GatewayQueryInputPort gatewayQueryInputPort;
    private final ServiceQueryServiceInputPort serviceQueryServiceInputPort;
    private final RouteQueryServiceInputPort routeQueryServiceInputPort;

    @Override
    public Mono<GatewayRuntimeResponse> getGatewayRuntime(UUID gatewayId) {
        return gatewayQueryInputPort.getGatewayById(gatewayId)
                .flatMap(this::aggregateRuntime);
    }

    @Override
    public Mono<GatewayRuntimeResponse> getGatewayRuntimeByTenantAndName(UUID tenantId, String gatewayName) {
        return gatewayQueryInputPort.getGatewayByTenantIdAndGatewayName(tenantId, gatewayName)
                .switchIfEmpty(gatewayQueryInputPort.getGatewayByTenantIdAndGatewayName(tenantId, gatewayName.replace("-", " ")))
                .flatMap(this::aggregateRuntime);
    }

    private Mono<GatewayRuntimeResponse> aggregateRuntime(GatewayDetailResponse gateway) {
        UUID gatewayId = gateway.gatewayId();
        Mono<List<ServiceRuntimeItem>> servicesMono = serviceQueryServiceInputPort.getServiceByGatewayId(gatewayId)
                .map(this::toServiceRuntimeItem)
                .collectList();

        Mono<List<RouteRuntimeItem>> routesMono = routeQueryServiceInputPort.getRouteByGatewayId(gatewayId)
                .map(this::toRouteRuntimeItem)
                .collectList();

        return Mono.zip(servicesMono, routesMono)
                .map(tuple -> new GatewayRuntimeResponse(
                        gateway.gatewayId(),
                        gateway.tenantId(),
                        gateway.gatewayName(),
                        gateway.gatewayType(),
                        gateway.authType(),
                        gateway.status(),
                        tuple.getT1(),
                        tuple.getT2()
                ));
    }

    private ServiceRuntimeItem toServiceRuntimeItem(ServiceResponse service) {
        return new ServiceRuntimeItem(
                service.serviceId(),
                service.gatewayId(),
                service.name(),
                service.type() == null ? null : service.type().name(),
                service.baseUrl(),
                service.status()
        );
    }

    private RouteRuntimeItem toRouteRuntimeItem(RouteResponse route) {
        return new RouteRuntimeItem(
                route.routeId(),
                route.gatewayId(),
                route.serviceId(),
                route.path(),
                route.method() == null ? null : route.method().name(),
                route.targetPath(),
                route.routeSecurity() == null ? null : route.routeSecurity().name(),
                route.status()
        );
    }
}
