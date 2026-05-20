package kh.edu.istad.stadoor.consumer.repository;

import kh.edu.istad.stadoor.consumer.entity.ApiKeyCredentialEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface ApiKeyCredentialReactiveRepository extends ReactiveCrudRepository<ApiKeyCredentialEntity, UUID> {}
