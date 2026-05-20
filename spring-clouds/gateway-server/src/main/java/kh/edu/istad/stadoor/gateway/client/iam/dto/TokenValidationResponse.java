package kh.edu.istad.stadoor.gateway.client.iam.dto;

public record TokenValidationResponse(
        boolean valid,
        String subject
) {
}
