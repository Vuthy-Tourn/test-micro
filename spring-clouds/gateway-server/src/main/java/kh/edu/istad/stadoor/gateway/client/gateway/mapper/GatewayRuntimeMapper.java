package kh.edu.istad.stadoor.gateway.client.gateway.mapper;

import kh.edu.istad.stadoor.gateway.client.gateway.dto.GatewayMetadataResponse;
import kh.edu.istad.stadoor.gateway.client.gateway.dto.RouteMetadataResponse;
import kh.edu.istad.stadoor.gateway.client.gateway.dto.ServiceMetadataResponse;
import kh.edu.istad.stadoor.gateway.runtime.model.GatewayRuntimeConfig;
import kh.edu.istad.stadoor.gateway.runtime.model.RouteRuntimeConfig;
import kh.edu.istad.stadoor.gateway.runtime.model.ServiceRuntimeConfig;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class GatewayRuntimeMapper {

    public GatewayRuntimeConfig toRuntimeConfig(GatewayMetadataResponse response) {
        List<RouteRuntimeConfig> allRoutes = safeList(response.routes()).stream()
                .map(this::toRouteRuntimeConfig)
                .toList();

        Map<UUID, List<RouteRuntimeConfig>> routesByServiceId = allRoutes.stream()
                .collect(java.util.stream.Collectors.groupingBy(RouteRuntimeConfig::serviceId));

        List<ServiceRuntimeConfig> services = safeList(response.services()).stream()
                .map(service -> toServiceRuntimeConfig(service, routesByServiceId))
                .toList();

        return new GatewayRuntimeConfig(
                response.gatewayId(),
                response.tenantId(),
                response.gatewayName(),
                response.description(),
                response.gatewayType(),
                response.authType(),
                response.status(),
                services
        );
    }

    private ServiceRuntimeConfig toServiceRuntimeConfig(
            ServiceMetadataResponse response,
            Map<UUID, List<RouteRuntimeConfig>> routesByServiceId
    ) {
        List<RouteRuntimeConfig> routes = routesByServiceId.getOrDefault(response.serviceId(), Collections.emptyList());
        return new ServiceRuntimeConfig(
                response.serviceId(),
                response.gatewayId(),
                response.name(),
                response.type(),
                response.baseUrl(),
                response.status(),
                routes
        );
    }

    private RouteRuntimeConfig toRouteRuntimeConfig(RouteMetadataResponse response) {
        return new RouteRuntimeConfig(
                response.routeId(),
                response.gatewayId(),
                response.serviceId(),
                response.path(),
                response.targetPath(),
                response.method(),
                response.routeSecurity(),
                response.status()
        );
    }

    private <T> List<T> safeList(List<T> value) {
        return value == null ? Collections.emptyList() : value;
    }
}
