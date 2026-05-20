package kh.edu.istad.stadoor.gateway.controller.route;

import kh.edu.istad.stadoor.gateway.route.dto.CreateRouteRequest;
import kh.edu.istad.stadoor.gateway.route.dto.CreateRouteResponse;
import kh.edu.istad.stadoor.gateway.route.dto.update.UpdateRouteRequest;
import kh.edu.istad.stadoor.gateway.route.dto.update.UpdateRouteResponse;
import kh.edu.istad.stadoor.gateway.route.ports.input.RouteCommandServiceInputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
public class RouteCommandController {
    private final RouteCommandServiceInputPort routeCommandServiceInputPort;

    @PostMapping
    public Mono<CreateRouteResponse> createRoute(@RequestBody CreateRouteRequest request) {
        return routeCommandServiceInputPort.createRoute(request);
    }

    @PatchMapping("/{routeId}")
    public Mono<UpdateRouteResponse> updateRoute(@PathVariable UUID routeId, @RequestBody UpdateRouteRequest request) {
        return routeCommandServiceInputPort.updateRoute(routeId, request);
    }

    @PatchMapping("/{routeId}/activate")
    Mono<String> activateService(@PathVariable UUID routeId) {
        return routeCommandServiceInputPort.activateRoute(routeId);
    }

    @PatchMapping("/{routeId}/deactivate")
    Mono<String> deactivateService(@PathVariable UUID routeId) {
        return routeCommandServiceInputPort.deactivateRoute(routeId);
    }
}
