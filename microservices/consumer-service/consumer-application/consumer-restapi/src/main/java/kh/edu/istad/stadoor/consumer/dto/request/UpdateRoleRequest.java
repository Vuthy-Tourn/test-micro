package kh.edu.istad.stadoor.consumer.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateRoleRequest(

        @NotBlank
        String name,

        String description
) {}
