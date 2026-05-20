package kh.edu.istad.stdoor.eureka.dto;

import lombok.Builder;

@Builder
public record LeasePolicy(

        int renewalIntervalSeconds,
        int expirationDurationSeconds
) {
}
