package kh.edu.istad.stadoor.gateway.aggregate;

import kh.edu.istad.stadoor.gateway.command.service.ActivateServiceCommand;
import kh.edu.istad.stadoor.gateway.command.service.DeactivateServiceCommand;
import kh.edu.istad.stadoor.gateway.command.service.RegisterServiceCommand;
import kh.edu.istad.stadoor.gateway.command.service.UpdateServiceCommand;
import kh.edu.istad.stadoor.gateway.event.service.ServiceActivatedEvent;
import kh.edu.istad.stadoor.gateway.event.service.ServiceDeactivatedEvent;
import kh.edu.istad.stadoor.gateway.event.service.ServiceRegisteredEvent;
import kh.edu.istad.stadoor.gateway.event.service.ServiceUpdatedEvent;
import kh.edu.istad.stadoor.gateway.validate.ServiceValidate;
import kh.edu.istad.stadoor.common.valueobject.gateway.GatewayId;
import kh.edu.istad.stadoor.gateway.valueobject.service.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.time.Instant;

@Getter
@NoArgsConstructor
@Aggregate
@Slf4j
@EqualsAndHashCode
public class ServiceAggregate {

    @AggregateIdentifier
    private ServiceId serviceId;

    private GatewayId gatewayId;
    private ServiceName name;
    private ServiceType type;
    private BaseUrl baseUrl;
    private ServiceStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    @CommandHandler
    public ServiceAggregate(RegisterServiceCommand registerServiceCommand) {
        log.info("Aggregate on RegisterServiceCommand{}", registerServiceCommand);

        ServiceValidate.validateRegister(
                registerServiceCommand.serviceId(),
                registerServiceCommand.gatewayId(),
                registerServiceCommand.name(),
                registerServiceCommand.type(),
                registerServiceCommand.baseUrl()
        );

        ServiceRegisteredEvent serviceRegisteredEvent = ServiceRegisteredEvent.builder()
                .serviceId(registerServiceCommand.serviceId())
                .gatewayId(registerServiceCommand.gatewayId())
                .name(registerServiceCommand.name())
                .type(registerServiceCommand.type())
                .status(ServiceStatus.ACTIVE)
                .baseUrl(registerServiceCommand.baseUrl())
                .createdAt(Instant.now())
                .build();

        AggregateLifecycle.apply(serviceRegisteredEvent);
    }

    @CommandHandler
    public void handle(UpdateServiceCommand updateServiceCommand) {
        log.info("Aggregate on UpdateServiceCommand{}", updateServiceCommand);

        ServiceValidate.validateUpdate(
                this.status,
                updateServiceCommand.name(),
                updateServiceCommand.baseUrl()
        );

        ServiceUpdatedEvent serviceUpdatedEvent = ServiceUpdatedEvent.builder()
                .serviceId(updateServiceCommand.serviceId())
                .name(updateServiceCommand.name())
                .baseUrl(updateServiceCommand.baseUrl())
                .serviceType(updateServiceCommand.type())
                .updatedAt(Instant.now())
                .build();

        AggregateLifecycle.apply(serviceUpdatedEvent);
    }

    @CommandHandler
    public void handle(ActivateServiceCommand activateServiceCommand) {
        log.info("Aggregate on ActivateServiceCommand{}", activateServiceCommand);

        ServiceActivatedEvent serviceActivatedEvent = ServiceActivatedEvent.builder()
                .serviceId(activateServiceCommand.serviceId())
                .status(ServiceStatus.ACTIVE)
                .updatedAt(Instant.now())
                .build();

        AggregateLifecycle.apply(serviceActivatedEvent);
    }

    @CommandHandler
    public void handle(DeactivateServiceCommand command) {
        log.info("Aggregate on DeactivateServiceCommand {}", command);

        ServiceValidate.validateDeactivate(this.status);

        ServiceDeactivatedEvent event = ServiceDeactivatedEvent.builder()
                .serviceId(command.serviceId())
                .status(ServiceStatus.INACTIVE)
                .updatedAt(Instant.now())
                .build();

        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(ServiceRegisteredEvent event) {
        this.serviceId = event.serviceId();
        this.gatewayId = event.gatewayId();
        this.name = event.name();
        this.type = event.type();
        this.baseUrl = event.baseUrl();
        this.status = event.status();
        this.createdAt = event.createdAt();
    }

    @EventSourcingHandler
    public void on(ServiceUpdatedEvent event) {
        this.serviceId = event.serviceId();
        this.name = event.name();
        this.baseUrl = event.baseUrl();
        this.type = event.serviceType();
        this.updatedAt = event.updatedAt();
    }

    @EventSourcingHandler
    public void on(ServiceActivatedEvent event) {
        this.serviceId = event.serviceId();
        this.status = event.status();
        this.updatedAt = event.updatedAt();

    }

    @EventSourcingHandler
    public void on(ServiceDeactivatedEvent event) {
        this.serviceId = event.serviceId();
        this.status = event.status();
        this.updatedAt = event.updatedAt();

    }
}
