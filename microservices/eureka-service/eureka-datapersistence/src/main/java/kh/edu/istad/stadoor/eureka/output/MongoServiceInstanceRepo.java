package kh.edu.istad.stadoor.eureka.output;



import kh.edu.istad.stadoor.eureka.entity.ServiceInstance;
import kh.edu.istad.stadoor.eureka.output.mapper.ServiceInstanceMapper;
import kh.edu.istad.stadoor.eureka.output.repository.SpringDataServiceInstanceRepo;
import kh.edu.istad.stadoor.eureka.port.output.ServiceInstanceRepositoryPort;
import kh.edu.istad.stadoor.eureka.valueobject.ApplicationName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MongoServiceInstanceRepo implements ServiceInstanceRepositoryPort {


    private final ServiceInstanceMapper serviceInstanceMapper;

    private final SpringDataServiceInstanceRepo springDataServiceInstanceRepo;


    @Override
    public Mono<ServiceInstance> save(ServiceInstance serviceInstance) {
        return Mono.just(serviceInstance)
                .map(serviceInstanceMapper::fromServiceInstance)
                .flatMap(springDataServiceInstanceRepo::save)
                .map(serviceInstanceMapper::toServiceInstanceDomain);
    }

    @Override
    public Mono<Void> deleteById(String serviceInstanceId) {
        return springDataServiceInstanceRepo.deleteById(serviceInstanceId);
    }

    @Override
    public Mono<ServiceInstance> findById(String serviceInstanceId) {
        // The repository must stay reactive end-to-end, so missing records are represented as an empty Mono.
        return springDataServiceInstanceRepo.findById(serviceInstanceId)
                .map(serviceInstanceMapper::toServiceInstanceDomain);
    }

    @Override
    public Flux<ServiceInstance> findAllInstances() {
        return springDataServiceInstanceRepo.findAll()
                .map(serviceInstanceMapper::toServiceInstanceDomain);
    }



    @Override
    public Mono<List<ServiceInstance>> findAllByApplicationName(ApplicationName applicationName) {
        return springDataServiceInstanceRepo.findAllByApplicationName(applicationName.value())
                .map(serviceInstanceMapper::toServiceInstanceDomain)
                .collectList();
    }
}
