package kh.edu.istad.stadoor.gateway.service.dto.update;

import kh.edu.istad.stadoor.gateway.valueobject.service.ServiceType;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UpdateServiceResponse(
        UUID serviceId,
        String name,
        String baseUrl,
        ServiceType type
) {
}
