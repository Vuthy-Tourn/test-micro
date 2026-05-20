package kh.edu.istad.stadoor.gateway.service;

import kh.edu.istad.stadoor.gateway.entity.Service;
import kh.edu.istad.stadoor.gateway.gateway.ports.output.GatewayRepositoryOutputPort;
import kh.edu.istad.stadoor.gateway.service.dto.ServiceOverviewResponse;
import kh.edu.istad.stadoor.gateway.service.dto.ServiceResponse;
import kh.edu.istad.stadoor.gateway.service.mapper.ServiceMapper;
import kh.edu.istad.stadoor.gateway.service.ports.input.ServiceQueryServiceInputPort;
import kh.edu.istad.stadoor.gateway.service.ports.output.ServiceRepository;
import kh.edu.istad.stadoor.gateway.valueobject.service.ServiceStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ServiceQueryServiceImpl implements ServiceQueryServiceInputPort {

    private final ServiceRepository serviceRepository;
    private final ServiceMapper serviceMapper;
    private final GatewayRepositoryOutputPort gatewayRepositoryOutputPort;

    @Override
    public Flux<ServiceResponse> getAllServices() {
        return serviceRepository.findAll()
//                .map(serviceMapper::serviceToServiceResponse);
                .flatMap(this::enrichWithGatewayName);
    }

//    @Override
//    public Flux<ServiceResponse> getServiceByGatewayId(UUID gatewayId) {
//        return serviceRepository.findByGatewayId(gatewayId)
//                .map(serviceMapper::serviceToServiceResponse);
//    }

    @Override
    public Flux<ServiceResponse> getServiceByGatewayId(UUID gatewayId) {
        return gatewayRepositoryOutputPort.findGatewayNameById(gatewayId)
                .flatMapMany(name ->
                        serviceRepository.findByGatewayId(gatewayId)
                                .map(s -> serviceMapper.serviceToServiceResponse(s, name))
                )
                .switchIfEmpty(
                        serviceRepository.findByGatewayId(gatewayId)
                                .map(s -> serviceMapper.serviceToServiceResponse(s, "Unknown"))
                );
    }

    @Override
    public Mono<ServiceResponse> getServiceById(UUID serviceId) {
        return serviceRepository.findById(serviceId)
//                .map(serviceMapper::serviceToServiceResponse);
                .flatMap(this::enrichWithGatewayName);
    }

    @Override
    public Mono<ServiceOverviewResponse> getServiceOverviewByGatewayId(UUID gatewayId) {
        return serviceRepository.findByGatewayId(gatewayId)
                .collectList()
                .map(services -> new ServiceOverviewResponse(
                        services.size(),
                        services.stream()
                                .filter(s -> s.getStatus() == ServiceStatus.ACTIVE)
                                .count(),
                        services.stream()
                                .filter(s -> s.getStatus() == ServiceStatus.INACTIVE)
                                .count()
                ));
    }
    private Mono<ServiceResponse> enrichWithGatewayName(Service service) {
        return gatewayRepositoryOutputPort
                .findGatewayNameById(service.getGatewayId().gatewayId())
                .map(name -> serviceMapper.serviceToServiceResponse(service, name))
                .defaultIfEmpty(serviceMapper.serviceToServiceResponse(service, "Unknown"));
    }
}
