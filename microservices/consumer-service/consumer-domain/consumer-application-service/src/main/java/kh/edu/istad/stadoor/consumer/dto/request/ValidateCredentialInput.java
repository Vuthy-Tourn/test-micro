package kh.edu.istad.stadoor.consumer.dto.request;

import kh.edu.istad.stadoor.consumer.valueobject.auth.AuthType;

import java.util.UUID;

public record ValidateCredentialInput(
        AuthType authType,
        String username,
        String password,
        String apiKey,
        String token,
        UUID consumerId,
        UUID gatewayId
) {}
