package kh.edu.istad.stadoor.gateway.service.ports.input;

import kh.edu.istad.stadoor.gateway.entity.Service;
import kh.edu.istad.stadoor.gateway.service.dto.ServiceOverviewResponse;
import kh.edu.istad.stadoor.gateway.service.dto.ServiceResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ServiceQueryServiceInputPort {
    Flux<ServiceResponse> getAllServices();
    Flux<ServiceResponse> getServiceByGatewayId(UUID gatewayId);
    Mono<ServiceResponse> getServiceById(UUID serviceId);
    Mono<ServiceOverviewResponse> getServiceOverviewByGatewayId(UUID gatewayId);

}
