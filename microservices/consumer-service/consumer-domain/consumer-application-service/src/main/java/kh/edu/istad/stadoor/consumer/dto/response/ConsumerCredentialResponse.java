package kh.edu.istad.stadoor.consumer.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ConsumerCredentialResponse(
        UUID credentialId,
        String credentialType,
        String value,
        String algorithm,
        Integer accessTokenTtl,
        Integer refreshTokenTtl,
        Instant createdAt
) {}
