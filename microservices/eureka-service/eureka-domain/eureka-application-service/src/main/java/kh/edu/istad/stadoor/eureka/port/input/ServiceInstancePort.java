package kh.edu.istad.stadoor.eureka.port.input;

import kh.edu.istad.stdoor.eureka.dto.ServiceInstanceCreateRequest;
import kh.edu.istad.stadoor.eureka.entity.ServiceInstance;
import kh.edu.istad.stdoor.eureka.event.ServiceInstanceCreatedEvent;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface ServiceInstancePort {

    Mono<ServiceInstance> save(ServiceInstanceCreateRequest instanceCreateRequest);

    Mono<ServiceInstance> findServiceInstanceByName(String applicationName);


    Mono<Void>Consume(ServiceInstanceCreatedEvent event);

    Mono<Map<String,Object>>deRegisterService(String applicationName,String instanceId);

    Mono<Void>reRegisterService();



}

