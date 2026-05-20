package kh.edu.istad.stadoor.gateway.controller.service;

import kh.edu.istad.stadoor.gateway.service.dto.ServiceOverviewResponse;
import kh.edu.istad.stadoor.gateway.service.dto.ServiceResponse;
import kh.edu.istad.stadoor.gateway.service.ports.input.ServiceQueryServiceInputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
public class ServiceQueryController {
    private final ServiceQueryServiceInputPort serviceQueryServiceInputPort;

    @GetMapping
    public Flux<ServiceResponse> getServicesByGatewayId( ) {
        return serviceQueryServiceInputPort.getAllServices();
    }

    @GetMapping("/gateways/{gatewayId}")
    public Flux<ServiceResponse> getServicesByGatewayId(
            @PathVariable UUID gatewayId
    ) {
        return serviceQueryServiceInputPort.getServiceByGatewayId(gatewayId);
    }

    @GetMapping("/overview/gateways/{gatewayId}")
    public Mono<ServiceOverviewResponse> getServicesOverviewByGatewayId(
            @PathVariable UUID gatewayId
    ) {
        return serviceQueryServiceInputPort.getServiceOverviewByGatewayId(gatewayId);
    }

    @GetMapping("/{serviceId}")
    Mono<ServiceResponse> getServiceById(@PathVariable UUID serviceId) {
        return serviceQueryServiceInputPort.getServiceById(serviceId);
    }
}
