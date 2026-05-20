package kh.edu.istad.stadoor.consumer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kh.edu.istad.stadoor.consumer.valueobject.auth.AuthType;
import kh.edu.istad.stadoor.consumer.valueobject.consumer.ConsumerType;

import java.util.UUID;

public record RegisterConsumerRequest(

        @NotNull
        UUID gatewayId,

        @NotNull
        UUID tenantId,

        @NotBlank @Size(max = 150)
        String name,

        @Size(max = 500)
        String description,

        @NotNull
        ConsumerType consumerType,

        @NotNull
        AuthType authType,

        String username,
        String password
) {}
