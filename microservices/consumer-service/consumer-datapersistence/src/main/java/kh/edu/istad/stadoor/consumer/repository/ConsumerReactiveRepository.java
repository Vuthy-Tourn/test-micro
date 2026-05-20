package kh.edu.istad.stadoor.consumer.repository;

import kh.edu.istad.stadoor.consumer.entity.ConsumerEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ConsumerReactiveRepository extends ReactiveCrudRepository<ConsumerEntity, UUID> {

    Flux<ConsumerEntity> findByGatewayId(UUID gatewayId);

    Flux<ConsumerEntity> findByTenantId(UUID tenantId);
}
