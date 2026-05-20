package kh.edu.istad.stadoor.consumer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateRoleRequest(

        @NotNull
        UUID tenantId,

        @NotBlank
        String name,

        String description
) {}
