package kh.edu.istad.stadoor.consumer.repository;

import kh.edu.istad.stadoor.consumer.entity.ConsumerCredentialEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface ConsumerCredentialReactiveRepository extends ReactiveCrudRepository<ConsumerCredentialEntity, UUID> {}
