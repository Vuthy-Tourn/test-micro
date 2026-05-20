package kh.edu.istad.stadoor.consumer.projection;

import java.util.UUID;

public record ConsumerCredentialProjection(
        UUID consumerId,
        UUID tenantId,
        UUID gatewayId,
        String credentialHash,
        String secretKey
) {}
