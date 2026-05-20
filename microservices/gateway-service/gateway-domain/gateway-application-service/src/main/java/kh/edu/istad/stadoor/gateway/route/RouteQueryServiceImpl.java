package kh.edu.istad.stadoor.gateway.route;

import kh.edu.istad.stadoor.gateway.route.dto.RouteResponse;
import kh.edu.istad.stadoor.gateway.route.dto.ServiceInRouteResponse;
import kh.edu.istad.stadoor.gateway.route.mapper.RouteMapper;
import kh.edu.istad.stadoor.gateway.route.ports.input.RouteQueryServiceInputPort;
import kh.edu.istad.stadoor.gateway.route.ports.output.RouteRepository;
import kh.edu.istad.stadoor.gateway.service.ports.output.ServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class RouteQueryServiceImpl implements RouteQueryServiceInputPort {

    private final RouteRepository routeRepository;
    private final ServiceRepository serviceRepository;
    private final RouteMapper routeMapper;

    @Override
    public Flux<RouteResponse> getAllRoutes() {
        return routeRepository.findAll()
                .flatMap(route ->
                        serviceRepository.findById(route.getServiceId().serviceId())
                                .map(service -> {
                                    return new RouteResponse(
                                            route.getRouteId().routeId(),
                                            route.getGatewayId().gatewayId(),
                                            route.getServiceId().serviceId(),

                                            new ServiceInRouteResponse(
                                                    service.getServiceId().serviceId(),
                                                    service.getServiceName().name(),
                                                    service.getStatus()
                                            ),

                                            route.getRoutePath().routePath(),
                                            route.getMethod(),
                                            route.getTargetPath().targetPath(),
                                            route.getSecure(),
                                            route.getStatus().name(),
                                            route.getCreatedAt(),
                                            route.getUpdatedAt()
                                    );
                                }));
    }
    @Override
    public Flux<RouteResponse> getRouteByGatewayId(UUID gatewayId) {
        return routeRepository.findByGatewayId(gatewayId)
                .flatMap(route ->
                        serviceRepository.findById(route.getServiceId().serviceId())
                                .map(service -> {
                                    return new RouteResponse(
                                            route.getRouteId().routeId(),
                                            route.getGatewayId().gatewayId(),
                                            route.getServiceId().serviceId(),

                                            new ServiceInRouteResponse(
                                                    service.getServiceId().serviceId(),
                                                    service.getServiceName().name(),
                                                    service.getStatus()
                                            ),

                                            route.getRoutePath().routePath(),
                                            route.getMethod(),
                                            route.getTargetPath().targetPath(),
                                            route.getSecure(),
                                            route.getStatus().name(),
                                            route.getCreatedAt(),
                                            route.getUpdatedAt()
                                    );
                                }));
    }

    @Override
    public Flux<RouteResponse> getRoutesByServiceId(UUID serviceId) {
        return routeRepository.findByServiceID(serviceId)
                .map(routeMapper::RouteToRouteResponse);
    }

    @Override
    public Mono<RouteResponse> getRouteById(UUID routeId) {
        return routeRepository.findByRouteId(routeId)
                .map(routeMapper::RouteToRouteResponse);
    }
}
