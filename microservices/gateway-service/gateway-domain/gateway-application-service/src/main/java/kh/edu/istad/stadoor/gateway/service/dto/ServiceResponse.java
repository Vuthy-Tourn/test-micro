package kh.edu.istad.stadoor.gateway.service.dto;

import kh.edu.istad.stadoor.gateway.valueobject.service.ServiceType;

import java.time.Instant;
import java.util.UUID;

public record ServiceResponse (
        UUID serviceId,
        UUID gatewayId,
        String gatewayName,
        String name,
        ServiceType type,
        String baseUrl,
        String status,
        Instant createdAt
){
}
