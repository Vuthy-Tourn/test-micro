package kh.edu.istad.stdoor.eureka.entity;

public record HealthCheckEndpoint(
        String healthUrl,
        String statusUrl,
        String homePageUrl
) {
}
