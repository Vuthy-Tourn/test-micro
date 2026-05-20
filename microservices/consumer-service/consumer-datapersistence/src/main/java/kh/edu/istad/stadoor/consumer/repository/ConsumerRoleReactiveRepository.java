package kh.edu.istad.stadoor.consumer.repository;

import kh.edu.istad.stadoor.consumer.entity.ConsumerRoleEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface ConsumerRoleReactiveRepository extends ReactiveCrudRepository<ConsumerRoleEntity, UUID> {
}
