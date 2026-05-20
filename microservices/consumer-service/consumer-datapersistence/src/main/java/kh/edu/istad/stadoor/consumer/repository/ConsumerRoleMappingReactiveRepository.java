package kh.edu.istad.stadoor.consumer.repository;

import kh.edu.istad.stadoor.consumer.entity.ConsumerRoleMappingEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface ConsumerRoleMappingReactiveRepository extends ReactiveCrudRepository<ConsumerRoleMappingEntity, UUID> {
}
