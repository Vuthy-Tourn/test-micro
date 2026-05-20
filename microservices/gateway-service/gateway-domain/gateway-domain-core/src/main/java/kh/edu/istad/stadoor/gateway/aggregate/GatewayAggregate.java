package kh.edu.istad.stadoor.gateway.aggregate;

import kh.edu.istad.stadoor.common.valueobject.TenantId;
import kh.edu.istad.stadoor.gateway.command.gateway.ActivateGatewayCommand;
import kh.edu.istad.stadoor.gateway.command.gateway.CreateGatewayCommand;
import kh.edu.istad.stadoor.gateway.command.gateway.DeactivateGatewayCommand;
import kh.edu.istad.stadoor.gateway.command.gateway.UpdateGatewayCommand;
import kh.edu.istad.stadoor.gateway.event.gateway.GatewayActivatedEvent;
import kh.edu.istad.stadoor.gateway.event.gateway.GatewayCreatedEvent;
import kh.edu.istad.stadoor.common.event.gateway.GatewayDeactivatedEvent;
import kh.edu.istad.stadoor.gateway.event.gateway.GatewayUpdatedEvent;
import kh.edu.istad.stadoor.gateway.valueobject.gateway.AuthType;
import kh.edu.istad.stadoor.gateway.valueobject.gateway.GatewayDescription;
import kh.edu.istad.stadoor.common.valueobject.gateway.GatewayId;
import kh.edu.istad.stadoor.gateway.valueobject.gateway.GatewayName;
import kh.edu.istad.stadoor.common.valueobject.gateway.GatewayStatus;
import kh.edu.istad.stadoor.gateway.valueobject.gateway.GatewayType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.time.ZonedDateTime;

@Aggregate
@Getter
@NoArgsConstructor
@EqualsAndHashCode
@Slf4j
public class GatewayAggregate {

    @AggregateIdentifier
    private GatewayId gatewayId;
    private TenantId tenantId;
    private GatewayName gatewayName;
    private GatewayDescription gatewayDescription;
    private GatewayStatus gatewayStatus;
    private AuthType authType;
    private GatewayType gatewayType;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    @CommandHandler
    public GatewayAggregate(CreateGatewayCommand createGatewayCommand) {
        log.info("Aggregate on creating gateway command {}", createGatewayCommand);

        validateGatewayName(createGatewayCommand.name());
        validateGatewayDescription(createGatewayCommand.description());
        validateGatewayType(createGatewayCommand.gatewayType());
        validateGatewayTypeAndAuth(createGatewayCommand.gatewayType(), createGatewayCommand.authType());

        GatewayCreatedEvent gatewayCreatedEvent = GatewayCreatedEvent.builder()
                .gatewayId(createGatewayCommand.gatewayId())
                .tenantId(createGatewayCommand.tenantId())
                .name(createGatewayCommand.name())
                .description(createGatewayCommand.description())
                .gatewayType(createGatewayCommand.gatewayType())
                .authType(createGatewayCommand.authType())
                .status(defaultStatus(createGatewayCommand.status()))
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();

        log.info("Created gateway event {}", gatewayCreatedEvent);
        AggregateLifecycle.apply(gatewayCreatedEvent);
    }

    @CommandHandler
    public void handle(UpdateGatewayCommand updateGatewayCommand) {
        log.info("Aggregate on updating gateway command {}", updateGatewayCommand);

        validateGatewayName(updateGatewayCommand.name());
        validateGatewayDescription(updateGatewayCommand.description());

        GatewayUpdatedEvent gatewayUpdatedEvent = GatewayUpdatedEvent.builder()
                .gatewayId(updateGatewayCommand.gatewayId())
                .name(updateGatewayCommand.name())
                .description(updateGatewayCommand.description())
                .updatedAt(ZonedDateTime.now())
                .build();

        log.info("Updated gateway event {}", gatewayUpdatedEvent);
        AggregateLifecycle.apply(gatewayUpdatedEvent);
    }

    @CommandHandler
    public void handle(ActivateGatewayCommand activateGatewayCommand) {
        log.info("Aggregate on activate gateway command {}", activateGatewayCommand);

        GatewayActivatedEvent gatewayActivatedEvent = GatewayActivatedEvent.builder()
                .gatewayId(activateGatewayCommand.gatewayId())
                .status(GatewayStatus.ACTIVE)
                .updatedAt(ZonedDateTime.now())
                .build();

        log.info("Activated gateway event {}", gatewayActivatedEvent);
        AggregateLifecycle.apply(gatewayActivatedEvent);
    }

    @CommandHandler
    public void handle(DeactivateGatewayCommand deactivateGatewayCommand) {
        log.info("Aggregate on deactivate gateway command {}", deactivateGatewayCommand);

        GatewayDeactivatedEvent gatewayDeactivatedEvent = GatewayDeactivatedEvent.builder()
                .gatewayId(deactivateGatewayCommand.gatewayId())
                .userId(deactivateGatewayCommand.userId())
                .status(GatewayStatus.INACTIVE)
                .updatedAt(ZonedDateTime.now())
                .build();

        log.info("Deactivated gateway event {}", gatewayDeactivatedEvent);
        AggregateLifecycle.apply(gatewayDeactivatedEvent);
    }

    @EventSourcingHandler
    public void on(GatewayCreatedEvent gatewayCreatedEvent) {
        this.gatewayId = gatewayCreatedEvent.gatewayId();
        this.tenantId = gatewayCreatedEvent.tenantId();
        this.gatewayName = gatewayCreatedEvent.name();
        this.gatewayDescription = gatewayCreatedEvent.description();
        this.gatewayType = gatewayCreatedEvent.gatewayType();
        this.authType = gatewayCreatedEvent.authType();
        this.gatewayStatus = gatewayCreatedEvent.status();
        this.createdAt = gatewayCreatedEvent.createdAt();
        this.updatedAt = gatewayCreatedEvent.updatedAt();
    }

    @EventSourcingHandler
    public void on(GatewayUpdatedEvent gatewayUpdatedEvent) {
        this.gatewayName = gatewayUpdatedEvent.name();
        this.gatewayDescription = gatewayUpdatedEvent.description();
        this.updatedAt = gatewayUpdatedEvent.updatedAt();
    }

    @EventSourcingHandler
    public void on(GatewayActivatedEvent gatewayActivatedEvent) {
        this.gatewayStatus = gatewayActivatedEvent.status();
        this.updatedAt = gatewayActivatedEvent.updatedAt();
    }

    @EventSourcingHandler
    public void on(GatewayDeactivatedEvent gatewayDeactivatedEvent) {
        this.gatewayStatus = gatewayDeactivatedEvent.status();
        this.updatedAt = gatewayDeactivatedEvent.updatedAt();
    }

    private GatewayStatus defaultStatus(GatewayStatus gatewayStatus) {
        return gatewayStatus != null ? gatewayStatus : GatewayStatus.INACTIVE;
    }

    private void validateGatewayName(GatewayName gatewayName) {
        if (gatewayName == null || gatewayName.name() == null || gatewayName.name().isBlank()) {
            throw new IllegalArgumentException("Gateway name is required.");
        }
    }

    private void validateGatewayDescription(GatewayDescription gatewayDescription) {
        if (gatewayDescription == null || gatewayDescription.description() == null || gatewayDescription.description().isBlank()) {
            throw new IllegalArgumentException("Gateway description is required.");
        }
    }

    private void validateGatewayType(GatewayType gatewayType) {
        if (gatewayType == null) {
            throw new IllegalArgumentException("Gateway type is required.");
        }
    }

    private void validateGatewayTypeAndAuth(GatewayType gatewayType, AuthType authType) {
        if (authType == null) {
            throw new IllegalArgumentException("Authentication type is required.");
        }

        if (GatewayType.BFF.equals(gatewayType) && !AuthType.OAUTH2.equals(authType)) {
            throw new IllegalArgumentException("BFF gateway must use OAUTH2 authentication.");
        }
    }
}
