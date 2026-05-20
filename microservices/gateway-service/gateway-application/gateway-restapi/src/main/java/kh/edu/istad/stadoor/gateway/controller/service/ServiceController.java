package kh.edu.istad.stadoor.gateway.controller.service;
import kh.edu.istad.stadoor.gateway.service.dto.ServiceResponse;
import kh.edu.istad.stadoor.gateway.service.dto.register.RegisterServiceRequest;
import kh.edu.istad.stadoor.gateway.service.dto.register.RegisterServiceResponse;
import kh.edu.istad.stadoor.gateway.service.dto.update.UpdateServiceRequest;
import kh.edu.istad.stadoor.gateway.service.dto.update.UpdateServiceResponse;
import kh.edu.istad.stadoor.gateway.service.ports.input.ServiceCommandServiceInputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceCommandServiceInputPort serviceCommandServiceInputPort;

    @PostMapping
    Mono<RegisterServiceResponse> registerService(@RequestBody RegisterServiceRequest request) {
        return serviceCommandServiceInputPort.registerService(request);
    }

    @PatchMapping("/{serviceId}")
    Mono<UpdateServiceResponse> updateService(@PathVariable UUID serviceId,
                                              @RequestBody UpdateServiceRequest request) {
        return serviceCommandServiceInputPort.updateService(serviceId,request);
    }

    @PatchMapping("/{serviceId}/activate")
    Mono<String> activateService(@PathVariable UUID serviceId) {
        return serviceCommandServiceInputPort.activateService(serviceId);
    }

    @PatchMapping("/{serviceId}/deactivate")
    Mono<String> deactivateService(@PathVariable UUID serviceId) {
        return serviceCommandServiceInputPort.deactivateService(serviceId);
    }





}
