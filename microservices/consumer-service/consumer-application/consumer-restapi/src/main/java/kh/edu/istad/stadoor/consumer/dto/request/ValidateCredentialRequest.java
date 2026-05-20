package kh.edu.istad.stadoor.consumer.dto.request;

import jakarta.validation.constraints.NotNull;
import kh.edu.istad.stadoor.consumer.valueobject.auth.AuthType;

import java.util.UUID;

public record ValidateCredentialRequest(
        @NotNull
        AuthType authType,
        String username,
        String password,
        String apiKey,
        String token,
        UUID consumerId,
        UUID gatewayId
) {}
