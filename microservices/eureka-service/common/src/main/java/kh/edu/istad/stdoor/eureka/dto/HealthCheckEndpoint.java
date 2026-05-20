package kh.edu.istad.stdoor.eureka.dto;

import lombok.Builder;

@Builder
public record HealthCheckEndpoint(
        String healthUrl,
        String statusUrl,
        String homePageUrl
) {
}
