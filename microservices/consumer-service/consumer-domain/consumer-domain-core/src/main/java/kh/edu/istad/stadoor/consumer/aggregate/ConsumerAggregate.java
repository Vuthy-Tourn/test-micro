package kh.edu.istad.stadoor.consumer.aggregate;

import kh.edu.istad.stadoor.consumer.command.ActivateConsumerCommand;
import kh.edu.istad.stadoor.consumer.command.DeactivateConsumerCommand;
import kh.edu.istad.stadoor.consumer.command.RegisterConsumerCommand;
import kh.edu.istad.stadoor.consumer.event.ConsumerActivatedEvent;
import kh.edu.istad.stadoor.consumer.event.ConsumerDeactivatedEvent;
import kh.edu.istad.stadoor.consumer.event.ConsumerRegisteredEvent;
import kh.edu.istad.stadoor.consumer.exception.DomainConflictException;
import kh.edu.istad.stadoor.consumer.valueobject.auth.AuthType;
import kh.edu.istad.stadoor.consumer.valueobject.consumer.ConsumerStatus;
import kh.edu.istad.stadoor.consumer.valueobject.consumer.ConsumerType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.UUID;

@Aggregate(snapshotTriggerDefinition = "consumerSnapshotTriggerDefinition")
@NoArgsConstructor
@Getter
@Slf4j
public class ConsumerAggregate {

    @AggregateIdentifier
    private UUID consumerId;

    private UUID tenantId;
    private UUID gatewayId;
    private String name;
    private String description;
    private ConsumerType consumerType;
    private AuthType authType;
    private ConsumerStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    @CommandHandler
    public ConsumerAggregate(RegisterConsumerCommand command) {
        log.info("Handle RegisterConsumerCommand: consumerId={}, authType={}", command.consumerId(), command.authType());

        AggregateLifecycle.apply(ConsumerRegisteredEvent.builder()
                .consumerId(command.consumerId())
                .tenantId(command.tenantId())
                .gatewayId(command.gatewayId())
                .name(command.name())
                .description(command.description())
                .consumerType(command.consumerType())
                .authType(command.authType())
                .status(ConsumerStatus.ACTIVE)
                .updatedAt(command.updatedAt())
                .createdAt(command.createdAt())
                .credentialId(command.credentialId())
                .credentialType(command.credentialType())
                .username(command.username())
                .credentialValue(command.credentialValue())
                .credentialHash(command.credentialHash())
                .algorithm(command.algorithm())
                .accessTokenTtl(command.accessTokenTtl())
                .refreshTokenTtl(command.refreshTokenTtl())
                .build());
    }

    @EventSourcingHandler
    public void on(ConsumerRegisteredEvent event) {
        this.consumerId = event.consumerId();
        this.tenantId = event.tenantId();
        this.gatewayId = event.gatewayId();
        this.name = event.name();
        this.description = event.description();
        this.consumerType = event.consumerType();
        this.authType = event.authType();
        this.status = event.status();
        this.createdAt = event.createdAt();
        this.updatedAt = event.updatedAt();
        log.info("on ConsumerRegisteredEvent: consumerId={}", event.consumerId());
    }

    @CommandHandler
    public void handle(ActivateConsumerCommand command) {
        log.info("Handle ActivateConsumerCommand: {}", command);

        if (this.status == ConsumerStatus.ACTIVE) {
            throw new DomainConflictException("Consumer is already active");
        }

        AggregateLifecycle.apply(ConsumerActivatedEvent.builder()
                .consumerId(this.consumerId)
                .status(ConsumerStatus.ACTIVE)
                .updatedAt(Instant.now())
                .build());
    }

    @EventSourcingHandler
    public void on (ConsumerActivatedEvent event) {
        this.consumerId = event.consumerId();
        this.status = event.status();
        this.updatedAt = event.updatedAt();
    }

    @CommandHandler
    public void handle(DeactivateConsumerCommand command) {
        log.info("handle DeactivateConsumerCommand: {}", command);

        if (this.status == ConsumerStatus.INACTIVE) {
            throw new DomainConflictException("Consumer is already deactivate");
        }

        AggregateLifecycle.apply(ConsumerDeactivatedEvent.builder()
                .consumerId(this.consumerId)
                .status(ConsumerStatus.INACTIVE)
                .updatedAt(Instant.now())
                .build());
    }

    @EventSourcingHandler
    public void on (ConsumerDeactivatedEvent event) {
        this.consumerId = event.consumerId();
        this.status = event.status();
        this.updatedAt = event.updatedAt();
    }
}
