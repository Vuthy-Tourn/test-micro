package kh.edu.istad.stadoor.eureka.entity;

import kh.edu.istad.stadoor.eureka.exception.InvalidServiceRegistrationException;
import kh.edu.istad.stadoor.eureka.valueobject.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ServiceInstance {

    private String uuid;
    private ApplicationName applicationName;
    private NetworkAddress networkAddress;
    private HealthCheckEndpoint healthCheckEndpoint;
    private LeasePolicy leasePolicy;
    private InstanceStatus status;
    private Metadata metadata;

    public ServiceInstance withStatus(InstanceStatus status) {
        return ServiceInstance.builder()
                .uuid(uuid)
                .applicationName(applicationName)
                .networkAddress(networkAddress)
                .healthCheckEndpoint(healthCheckEndpoint)
                .leasePolicy(leasePolicy)
                .status(status)
                .metadata(metadata)
                .build();
    }

    public void validateForRegistration() {
        requireText(uuid == null ? null : this.uuid, "service instance id is required");
        requireText(applicationName == null ? null : applicationName.value(), "application name is required");
//        requireLowercase(
//                applicationName.value(),
//                "application name must be uppercase and use letters, digits, '-' or '_'"
//        );

        if (networkAddress == null) {
            throw new InvalidServiceRegistrationException("network address is required");
        }

        requireText(networkAddress.hostName(), "host name is required");
        validatePort(networkAddress.port(), "port must be between 1 and 65535");

        if (healthCheckEndpoint == null) {
            throw new InvalidServiceRegistrationException("health check endpoints are required");
        }

        validateUrl(healthCheckEndpoint.healthUrl(), "health url must be a valid absolute http/https url");
        validateUrl(healthCheckEndpoint.statusUrl(), "status url must be a valid absolute http/https url");
        validateUrl(healthCheckEndpoint.homePageUrl(), "home page url must be a valid absolute http/https url");

        if (leasePolicy == null) {
            throw new InvalidServiceRegistrationException("lease policy is required");
        }

        if (leasePolicy.renewalIntervalSeconds() <= 0) {
            throw new InvalidServiceRegistrationException("renewal interval must be greater than zero");
        }

        if (leasePolicy.expirationDurationSeconds() <= leasePolicy.renewalIntervalSeconds()) {
            throw new InvalidServiceRegistrationException("expiration duration must be greater than renewal interval");
        }
    }

    private void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new InvalidServiceRegistrationException(message);
        }
    }

    private void requireLowercase(String value, String message) {
        if (!Objects.equals(value, value.toLowerCase()) || !value.matches("[A-Z0-9_-]+")) {
            throw new InvalidServiceRegistrationException(message);
        }
    }

    private void validatePort(int port, String message) {
        if (port < 1 || port > 65535) {
            throw new InvalidServiceRegistrationException(message);
        }
    }

    private void validateUrl(String value, String message) {
        requireText(value, message);

        try {
            URI uri = new URI(value);
            String scheme = uri.getScheme();
            if (!uri.isAbsolute() || (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme))) {
                throw new InvalidServiceRegistrationException(message);
            }
        } catch (URISyntaxException ex) {
            throw new InvalidServiceRegistrationException(message, ex);
        }
    }
}
