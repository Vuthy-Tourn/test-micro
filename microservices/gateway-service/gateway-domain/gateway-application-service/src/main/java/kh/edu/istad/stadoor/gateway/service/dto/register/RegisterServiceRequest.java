package kh.edu.istad.stadoor.gateway.service.dto.register;

import kh.edu.istad.stadoor.gateway.valueobject.service.ServiceType;

import java.util.UUID;

public record RegisterServiceRequest(
        UUID gatewayId,
        String name,
        ServiceType type,
        String baseUrl
//        ServiceStatus status
) {
}
