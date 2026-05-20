package kh.edu.istad.stadoor.gateway.client.consumer.dto;

import java.util.UUID;

public record ValidateConsumerCredentialRequest(
        String authType,
        String username,
        String password,
        String apiKey,
        String token,
        UUID consumerId,
        UUID gatewayId
) {
}
