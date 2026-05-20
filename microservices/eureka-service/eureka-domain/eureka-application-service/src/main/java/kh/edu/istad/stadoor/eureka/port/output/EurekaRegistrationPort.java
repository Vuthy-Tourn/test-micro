package kh.edu.istad.stadoor.eureka.port.output;

import kh.edu.istad.stadoor.eureka.entity.ServiceInstance;
import reactor.core.publisher.Mono;

public interface EurekaRegistrationPort {

    Mono<Void> register(ServiceInstance serviceInstance);
   Mono<Void>  renewLease(ServiceInstance serviceInstance);
   Mono<Void>deRegister(String applicationName,String instanceId);

}
