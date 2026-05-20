package kh.edu.istad.stadoor.gateway.controller.route;

import kh.edu.istad.stadoor.gateway.route.dto.RouteResponse;
import kh.edu.istad.stadoor.gateway.route.ports.input.RouteQueryServiceInputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/routes/query")
@RequiredArgsConstructor
public class RouteQueryController {

    private final RouteQueryServiceInputPort routeQueryServiceInputPort;

    @GetMapping
    public Flux<RouteResponse> getAllRoutes() {
        return routeQueryServiceInputPort.getAllRoutes();
    }

    @GetMapping("/{routeId}")
    public Mono<RouteResponse> getRouteById(@PathVariable UUID routeId) {
        return routeQueryServiceInputPort.getRouteById(routeId);
    }

    @GetMapping("/gateway/{gatewayId}")
    public Flux<RouteResponse> getRouteByGatewayId(@PathVariable UUID gatewayId) {
        return routeQueryServiceInputPort.getRouteByGatewayId(gatewayId);
    }   

    @GetMapping("/service/{serviceId}")
    public Flux<RouteResponse> getRoutesByServiceId(@PathVariable UUID serviceId) {
        return routeQueryServiceInputPort.getRoutesByServiceId(serviceId);
    }
}
