package kh.edu.istad.stadoor.eureka.entity;

import kh.edu.istad.stadoor.eureka.exception.InvalidServiceRegistrationException;
import kh.edu.istad.stadoor.eureka.valueobject.ApplicationName;
import kh.edu.istad.stadoor.eureka.valueobject.HealthCheckEndpoint;
import kh.edu.istad.stadoor.eureka.valueobject.InstanceStatus;
import kh.edu.istad.stadoor.eureka.valueobject.LeasePolicy;
import kh.edu.istad.stadoor.eureka.valueobject.NetworkAddress;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ServiceInstanceRegistrationValidationTest {

    @Test
    void shouldAllowValidRegistration() {
        ServiceInstance serviceInstance = validServiceInstance();

        assertDoesNotThrow(serviceInstance::validateForRegistration);
    }

    @Test
    void shouldNormalizeApplicationNameToUppercase() {
        ApplicationName applicationName = new ApplicationName("gateway-service");

        assertEquals("GATEWAY-SERVICE", applicationName.value());
    }

    @Test
    void shouldRejectApplicationNameWithUnsupportedCharacters() {
        ServiceInstance serviceInstance = ServiceInstance.builder()
                .uuid("instance-1")
                .applicationName(new ApplicationName("gateway service"))
                .networkAddress(new NetworkAddress("gateway-1", "10.0.0.10", 8080, 8443, true))
                .healthCheckEndpoint(validHealthCheckEndpoint())
                .leasePolicy(new LeasePolicy(30, 90))
                .status(InstanceStatus.UP)
                .build();

        InvalidServiceRegistrationException exception = assertThrows(
                InvalidServiceRegistrationException.class,
                serviceInstance::validateForRegistration
        );

        assertEquals("application name must use only letters, digits, '-' or '_'", exception.getMessage());
    }

    @Test
    void shouldRejectInvalidLeasePolicy() {
        ServiceInstance serviceInstance = ServiceInstance.builder()
                .uuid("instance-1")
                .applicationName(new ApplicationName("GATEWAY_SERVICE"))
                .networkAddress(new NetworkAddress("gateway-1", "10.0.0.10", 8080, 8443, true))
                .healthCheckEndpoint(validHealthCheckEndpoint())
                .leasePolicy(new LeasePolicy(30, 20))
                .status(InstanceStatus.UP)
                .build();

        InvalidServiceRegistrationException exception = assertThrows(
                InvalidServiceRegistrationException.class,
                serviceInstance::validateForRegistration
        );

        assertEquals("expiration duration must be greater than renewal interval", exception.getMessage());
    }

    private ServiceInstance validServiceInstance() {
        return ServiceInstance.builder()
                .uuid("instance-1")
                .applicationName(new ApplicationName("gateway_service"))
                .networkAddress(new NetworkAddress("gateway-1", "10.0.0.10", 8080, 8443, true))
                .healthCheckEndpoint(validHealthCheckEndpoint())
                .leasePolicy(new LeasePolicy(30, 90))
                .status(InstanceStatus.UP)
                .build();
    }

    private HealthCheckEndpoint validHealthCheckEndpoint() {
        return HealthCheckEndpoint.builder()
                .healthUrl("http://gateway-1:8080/actuator/health")
                .statusUrl("http://gateway-1:8080/actuator/info")
                .homePageUrl("http://gateway-1:8080")
                .build();
    }
}

