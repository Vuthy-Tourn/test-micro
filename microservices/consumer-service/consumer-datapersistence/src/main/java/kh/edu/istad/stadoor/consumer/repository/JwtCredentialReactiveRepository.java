package kh.edu.istad.stadoor.consumer.repository;

import kh.edu.istad.stadoor.consumer.entity.JwtCredentialEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface JwtCredentialReactiveRepository extends ReactiveCrudRepository<JwtCredentialEntity, UUID> {}
