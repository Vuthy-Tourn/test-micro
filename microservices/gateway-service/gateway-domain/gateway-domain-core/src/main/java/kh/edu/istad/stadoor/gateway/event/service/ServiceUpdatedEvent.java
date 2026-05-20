package kh.edu.istad.stadoor.gateway.event.service;

import kh.edu.istad.stadoor.gateway.valueobject.service.BaseUrl;
import kh.edu.istad.stadoor.gateway.valueobject.service.ServiceId;
import kh.edu.istad.stadoor.gateway.valueobject.service.ServiceName;
import kh.edu.istad.stadoor.gateway.valueobject.service.ServiceType;
import lombok.Builder;

import java.time.Instant;
import java.time.ZonedDateTime;

@Builder
public record ServiceUpdatedEvent(
        ServiceId serviceId,
        ServiceName name,
        BaseUrl baseUrl,
        ServiceType serviceType,
        Instant updatedAt
) {
}