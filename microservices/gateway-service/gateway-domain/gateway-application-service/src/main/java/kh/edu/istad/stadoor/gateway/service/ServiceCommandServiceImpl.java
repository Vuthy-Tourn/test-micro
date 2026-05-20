package kh.edu.istad.stadoor.gateway.service;

import kh.edu.istad.stadoor.gateway.command.service.ActivateServiceCommand;
import kh.edu.istad.stadoor.gateway.command.service.DeactivateServiceCommand;
import kh.edu.istad.stadoor.gateway.command.service.RegisterServiceCommand;
import kh.edu.istad.stadoor.gateway.command.service.UpdateServiceCommand;
import kh.edu.istad.stadoor.gateway.gateway.exception.GatewayNotFoundException;
import kh.edu.istad.stadoor.gateway.gateway.ports.output.GatewayRepositoryOutputPort;
import kh.edu.istad.stadoor.gateway.service.exception.ServiceAlreadyExistsException;
import kh.edu.istad.stadoor.gateway.service.mapper.ServiceMapper;
import kh.edu.istad.stadoor.gateway.service.ports.output.ServiceRepository;
import kh.edu.istad.stadoor.gateway.valueobject.service.ServiceId;
import kh.edu.istad.stadoor.gateway.service.dto.register.RegisterServiceRequest;
import kh.edu.istad.stadoor.gateway.service.dto.register.RegisterServiceResponse;
import kh.edu.istad.stadoor.gateway.service.dto.update.UpdateServiceRequest;
import kh.edu.istad.stadoor.gateway.service.dto.update.UpdateServiceResponse;
import kh.edu.istad.stadoor.gateway.service.ports.input.ServiceCommandServiceInputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;



@Slf4j
@RequiredArgsConstructor
@Service
public class ServiceCommandServiceImpl implements ServiceCommandServiceInputPort {

    private final CommandGateway commandGateway;
    private final ServiceMapper serviceApplicationMapper;
    private final GatewayRepositoryOutputPort gatewayRepositoryOutputPort;
    private final ServiceRepository serviceRepository;

    @Override
    public Mono<RegisterServiceResponse> registerService(RegisterServiceRequest registerServiceRequest) {

        return gatewayRepositoryOutputPort
                .existByGatewayId(registerServiceRequest.gatewayId())
                .flatMap(gatewayExists -> {
                    if (!gatewayExists) {
                        return Mono.error(
                                new GatewayNotFoundException("Gateway not found")
                        );
                    }

                    return serviceRepository.existsByGatewayIdAndName(
                            registerServiceRequest.gatewayId(),
                            registerServiceRequest.name()
                    );
                })
                .flatMap(serviceExists -> {
                    if (serviceExists) {
                        return Mono.error(
                                new ServiceAlreadyExistsException(
                                        "Service name already exists in this gateway"
                                )
                        );
                    }

                    ServiceId serviceId = new ServiceId(UUID.randomUUID());

                    RegisterServiceCommand command =
                            serviceApplicationMapper
                                    .registerServiceRequestToRegisterServiceCommand(
                                            serviceId,
                                            registerServiceRequest
                                    );

                    return Mono.fromFuture(commandGateway.send(command))
                            .thenReturn(
                                    RegisterServiceResponse.builder()
                                            .serviceId(serviceId.serviceId())
                                            .gatewayId(command.gatewayId().gatewayId())
                                            .name(command.name().name())
                                            .type(command.type())
                                            .baseUrl(command.baseUrl().baseUrl())
                                            .message("Registered service successfully")
                                            .build()
                            );
                });}
    @Override
    public Mono<UpdateServiceResponse> updateService(UUID serviceId, UpdateServiceRequest updateServiceRequest) {

        UpdateServiceCommand updateServiceCommand =
                serviceApplicationMapper.updateServiceRequestToUpdateServiceCommand(serviceId, updateServiceRequest);

        log.info("Updating service command {}", updateServiceCommand);

        try {
            commandGateway.send(updateServiceCommand);
        } catch (CommandExecutionException e) {
            log.error("Update command failed: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update service: " + e.getMessage(), e);
        }

        return Mono.just(UpdateServiceResponse.builder()
                .serviceId(serviceId)
                .name(updateServiceRequest.name())
                .baseUrl(updateServiceRequest.baseUrl())
                .type(updateServiceRequest.type())
                .build());
    }

    @Override
    public Mono<String> activateService(UUID serviceId) {
        ActivateServiceCommand command = new ActivateServiceCommand(new ServiceId(serviceId));

        try {
            commandGateway.send(command);
        } catch (CommandExecutionException e) {
            log.error("Activate command failed: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to activate service: " + e.getMessage(), e);
        }

        return Mono.just("Service activated");
    }

    @Override
    public Mono<String> deactivateService(UUID serviceId) {
        DeactivateServiceCommand command = new DeactivateServiceCommand(new ServiceId(serviceId));

        try {
            commandGateway.send(command);
        } catch (CommandExecutionException e) {
            log.error("Deactivate command failed: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to deactivate service: " + e.getMessage(), e);
        }

        return Mono.just("Service deactivated");
    }
}
