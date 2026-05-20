package kh.edu.istad.stadoor.gateway.command.service;

import kh.edu.istad.stadoor.gateway.valueobject.service.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record UpdateServiceCommand(

        @TargetAggregateIdentifier
        ServiceId serviceId,

        ServiceName name,
        BaseUrl baseUrl,
        ServiceType type
) {
}
