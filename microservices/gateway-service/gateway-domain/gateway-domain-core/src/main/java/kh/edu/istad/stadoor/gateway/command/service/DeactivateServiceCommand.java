package kh.edu.istad.stadoor.gateway.command.service;

import kh.edu.istad.stadoor.gateway.valueobject.service.ServiceId;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record DeactivateServiceCommand(
        @TargetAggregateIdentifier
        ServiceId serviceId
) {
}
