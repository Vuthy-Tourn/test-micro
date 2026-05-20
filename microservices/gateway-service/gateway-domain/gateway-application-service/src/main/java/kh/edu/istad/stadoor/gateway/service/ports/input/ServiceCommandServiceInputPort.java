package kh.edu.istad.stadoor.gateway.service.ports.input;

import kh.edu.istad.stadoor.gateway.service.dto.ServiceResponse;
import kh.edu.istad.stadoor.gateway.service.dto.register.RegisterServiceRequest;
import kh.edu.istad.stadoor.gateway.service.dto.register.RegisterServiceResponse;
import kh.edu.istad.stadoor.gateway.service.dto.update.UpdateServiceRequest;
import kh.edu.istad.stadoor.gateway.service.dto.update.UpdateServiceResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;


public interface ServiceCommandServiceInputPort {
    Mono<RegisterServiceResponse> registerService(RegisterServiceRequest registerServiceRequest);
    Mono<UpdateServiceResponse> updateService(UUID serviceId, UpdateServiceRequest updateServiceRequest);
    Mono<String> activateService(UUID serviceId);
    Mono<String> deactivateService(UUID serviceId);


}
