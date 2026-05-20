package kh.edu.istad.stadoor.gateway.service.adapter;

import kh.edu.istad.stadoor.gateway.entity.Service;
import kh.edu.istad.stadoor.gateway.service.entity.ServiceEntity;
import kh.edu.istad.stadoor.gateway.service.repository.ServiceR2dbcRepository;
import kh.edu.istad.stadoor.gateway.service.mapper.ServiceDataAccessMapper;
import kh.edu.istad.stadoor.gateway.service.ports.output.ServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ServiceRepositoryAdapter implements ServiceRepository {

    private final ServiceR2dbcRepository repository;
    private final ServiceDataAccessMapper serviceDataAccessMapper;

    @Override
    public Mono<Service> save(Service service) {

        ServiceEntity entity = serviceDataAccessMapper.serviceToServiceEntity(service);
        entity.setNew(entity.getUpdatedAt() == null);
        log.info(entity.toString());

        System.out.println("Entity to save: " + entity);

        return repository.save(entity)
                .doOnSuccess(saved -> System.out.println("Saved into DB: " + saved))
                .doOnError(Throwable::printStackTrace)
                .map(saved -> service);
    }

    @Override
    public Mono<Service> findById(UUID serviceId) {


        return repository.findById(serviceId)
                .switchIfEmpty(Mono.error(new RuntimeException("Service not found")))
                .map(serviceDataAccessMapper::serviceEntityToService)
                .doOnNext(service -> System.out.println("Found service: " + service));
    }

    @Override
    public Flux<Service> findByGatewayId(UUID gatewayId) {

        return repository.findByGatewayId(gatewayId)
                .map(serviceDataAccessMapper::serviceEntityToService)
                .doOnNext(service -> log.info("Found service: {}", service))
                .doOnError(Throwable::printStackTrace);
    }

    @Override
    public Flux<Service> findAll() {
        return repository.findAll()
                .map(serviceDataAccessMapper::serviceEntityToService);
    }

    @Override
    public Mono<Boolean> existsByGatewayIdAndName(UUID gatewayId, String name) {
        return repository.existsByGatewayIdAndName(gatewayId, name);
    }

    @Override
    public Mono<Boolean> existsById(UUID serviceId) {
        return repository.existsById(serviceId);
    }
}