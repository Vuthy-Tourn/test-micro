package kh.edu.istad.stdoor.eureka.entity;

public record LeasePolicy(
        int renewalIntervalSeconds,
        int expirationDurationSeconds
) {
}
