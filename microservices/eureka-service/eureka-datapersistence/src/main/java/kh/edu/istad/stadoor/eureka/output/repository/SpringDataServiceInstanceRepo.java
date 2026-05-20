package kh.edu.istad.stadoor.eureka.output.repository;

import kh.edu.istad.stdoor.eureka.entity.ServiceInstanceEntity;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface SpringDataServiceInstanceRepo extends ReactiveMongoRepository<ServiceInstanceEntity, String> {

    Flux<kh.edu.istad.stdoor.eureka.entity.ServiceInstanceEntity> findAllByApplicationName(String applicationName);

}
