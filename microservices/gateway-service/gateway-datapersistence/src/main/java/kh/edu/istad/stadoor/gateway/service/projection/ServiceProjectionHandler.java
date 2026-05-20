package kh.edu.istad.stadoor.gateway.service.projection;

import kh.edu.istad.stadoor.gateway.entity.Service;
import kh.edu.istad.stadoor.gateway.event.service.ServiceActivatedEvent;
import kh.edu.istad.stadoor.gateway.event.service.ServiceDeactivatedEvent;
import kh.edu.istad.stadoor.gateway.event.service.ServiceRegisteredEvent;
import kh.edu.istad.stadoor.gateway.event.service.ServiceUpdatedEvent;
import kh.edu.istad.stadoor.gateway.service.ports.output.ServiceRepository;
import kh.edu.istad.stadoor.gateway.valueobject.service.ServiceStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ServiceProjectionHandler {

    private final ServiceRepository serviceRepository;


    @EventHandler
    public void on(ServiceRegisteredEvent event) {

        Service service = new Service();
        service.setServiceId(event.serviceId());
        service.setGatewayId(event.gatewayId());
        service.setServiceName(event.name());
        service.setServiceType(event.type());
        service.setStatus(event.status());
        service.setBaseUrl(event.baseUrl());
        service.setCreatedAt(event.createdAt());

        serviceRepository.save(service)
                .doOnSuccess(saved -> log.info("Service saved: {}", saved))
                .doOnError(error -> log.error("Error saving service", error))
                .subscribe();
    }


    @EventHandler
    public void on(ServiceUpdatedEvent event) {

        UUID serviceId = event.serviceId().serviceId();

        serviceRepository.findById(serviceId)
                .flatMap(service -> {
                    service.setServiceName(event.name());
                    service.setBaseUrl(event.baseUrl());
                    service.setServiceType(event.serviceType());
                    service.setUpdatedAt(event.updatedAt());
                    return serviceRepository.save(service);
                })
                .doOnSuccess(updated -> log.info("Service updated: {}", updated))
                .doOnError(error -> log.error("Error updating service", error))
                .subscribe();
    }


    @EventHandler
    public void on(ServiceActivatedEvent event) {
        updateStatus(event.serviceId().serviceId(), event.status(), event.updatedAt());
    }


    @EventHandler
    public void on(ServiceDeactivatedEvent event) {
        updateStatus(event.serviceId().serviceId(), event.status(), event.updatedAt());
    }

    // Shared logic
    private void updateStatus(UUID serviceId, ServiceStatus status, Instant updatedAt) {

        serviceRepository.findById(serviceId)
                .flatMap(service -> {
                    service.setStatus(status);
                    service.setUpdatedAt(updatedAt != null ? updatedAt : Instant.now());
                    return serviceRepository.save(service);
                })
                .doOnSuccess(updated -> log.info("Service status updated: {}", updated))
                .doOnError(error -> log.error("Error updating status", error))
                .subscribe();
    }


}