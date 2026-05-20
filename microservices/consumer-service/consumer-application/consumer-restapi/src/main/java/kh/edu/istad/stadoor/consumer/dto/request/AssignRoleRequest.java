package kh.edu.istad.stadoor.consumer.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignRoleRequest(

        @NotNull
        UUID consumerId,

        @NotNull
        UUID roleId,

        @NotNull
        UUID assignedByUserId
) {}
