package kh.edu.istad.stadoor.gateway.service.ports.output;

import kh.edu.istad.stadoor.gateway.entity.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ServiceRepository {

    Mono<Service> save(Service service);
    Mono<Service> findById(UUID serviceId);
    Flux<Service> findByGatewayId(UUID gatewayId);
    Flux<Service> findAll();
    Mono<Boolean> existsById(UUID serviceId);
    Mono<Boolean> existsByGatewayIdAndName(UUID gatewayId, String name);
}