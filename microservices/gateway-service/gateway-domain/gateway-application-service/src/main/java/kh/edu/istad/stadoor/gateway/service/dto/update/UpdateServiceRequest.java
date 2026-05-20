package kh.edu.istad.stadoor.gateway.service.dto.update;

import kh.edu.istad.stadoor.gateway.valueobject.service.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record UpdateServiceRequest(
        String name,
        String baseUrl,
        ServiceType type
) {
}
