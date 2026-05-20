package kh.edu.istad.stadoor.consumer.dto.response;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record ValidateCredentialResponse(
        boolean valid,
        UUID consumerId,
        UUID tenantId,
        UUID gatewayId,
        List<String> roles
) {}
