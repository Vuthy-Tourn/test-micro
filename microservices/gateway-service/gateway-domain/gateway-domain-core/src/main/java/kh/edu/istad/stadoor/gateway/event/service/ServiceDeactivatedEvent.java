package kh.edu.istad.stadoor.gateway.event.service;

import kh.edu.istad.stadoor.gateway.valueobject.service.ServiceId;
import kh.edu.istad.stadoor.gateway.valueobject.service.ServiceStatus;
import lombok.Builder;

import java.time.Instant;
import java.time.ZonedDateTime;

@Builder
public record ServiceDeactivatedEvent(
        ServiceId serviceId,
        ServiceStatus status,
        Instant updatedAt
) {
}