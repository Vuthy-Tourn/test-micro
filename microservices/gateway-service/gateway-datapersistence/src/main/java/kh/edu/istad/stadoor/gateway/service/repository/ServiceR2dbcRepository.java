package kh.edu.istad.stadoor.gateway.service.repository;

import kh.edu.istad.stadoor.gateway.service.entity.ServiceEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ServiceR2dbcRepository
        extends R2dbcRepository<ServiceEntity, UUID> {
        Flux<ServiceEntity> findByGatewayId(UUID gatewayId);
        Mono<Boolean> existsByGatewayIdAndName (UUID gatewayId, String name);
}
