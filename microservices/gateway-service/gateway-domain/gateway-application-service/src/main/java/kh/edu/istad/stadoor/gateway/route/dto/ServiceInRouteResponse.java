package kh.edu.istad.stadoor.gateway.route.dto;

import kh.edu.istad.stadoor.gateway.valueobject.service.ServiceStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ServiceInRouteResponse (
        UUID serviceId,
        String serviceName,
        ServiceStatus status
){
}
