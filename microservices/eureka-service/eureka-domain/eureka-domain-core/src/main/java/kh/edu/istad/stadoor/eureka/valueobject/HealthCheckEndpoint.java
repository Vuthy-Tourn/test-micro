package kh.edu.istad.stadoor.eureka.valueobject;


import lombok.Builder;

@Builder
public record HealthCheckEndpoint(
        String healthUrl,
        String statusUrl,
        String homePageUrl
) {
}
