package kh.edu.istad.stadoor.eureka.port.output;



import kh.edu.istad.stadoor.eureka.entity.ServiceInstance;
import kh.edu.istad.stadoor.eureka.valueobject.ApplicationName;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


public interface ServiceInstanceRepositoryPort {

    Mono<ServiceInstance> save(ServiceInstance serviceInstance);

    Mono<Void> deleteById(String serviceInstanceId);

    Mono<ServiceInstance> findById(String serviceInstanceId);

    Flux<ServiceInstance>findAllInstances();

    Mono<List<ServiceInstance>> findAllByApplicationName(ApplicationName applicationName);
}
