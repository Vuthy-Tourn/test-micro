package kh.edu.istad.stadoor.consumer.repository;

import kh.edu.istad.stadoor.consumer.entity.BasicAuthCredentialEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface BasicAuthCredentialReactiveRepository extends ReactiveCrudRepository<BasicAuthCredentialEntity, UUID> {}
