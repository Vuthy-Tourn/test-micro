package kh.edu.istad.stadoor.gateway.client.consumer.dto;

import java.util.List;
import java.util.UUID;

public record ApiKeyValidationResponse(
        boolean valid,
        UUID consumerId,
        UUID tenantId,
        UUID gatewayId,
        List<String> roles
) {
}
