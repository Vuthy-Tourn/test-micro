package kh.edu.istad.stadoor.eureka.valueobject;

import lombok.Builder;

@Builder
public record LeasePolicy(
        int renewalIntervalSeconds,
        int expirationDurationSeconds
) {
}
