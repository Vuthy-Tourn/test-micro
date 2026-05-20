package kh.edu.istad.stadoor.gateway.service.dto.register;

import kh.edu.istad.stadoor.gateway.valueobject.service.ServiceType;
import lombok.Builder;

import java.util.UUID;

@Builder
public record RegisterServiceResponse(
        UUID serviceId,
        UUID gatewayId,
        String name,
        ServiceType type,
//        String status,
        String baseUrl,
        String message
) {
}
