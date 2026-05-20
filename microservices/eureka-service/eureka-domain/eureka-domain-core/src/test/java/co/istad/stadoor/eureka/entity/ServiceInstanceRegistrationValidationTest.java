package co.istad.stadoor.eureka.entity;

import co.istad.stadoor.eureka.entity.EurekaAggregate;
import kh.edu.istad.stadoor.eureka.exception.InvalidServiceRegistrationException;
import kh.edu.istad.stadoor.eureka.valueobject.ApplicationName;
import kh.edu.istad.stadoor.eureka.valueobject.HealthCheckEndpoint;
import kh.edu.istad.stadoor.eureka.valueobject.InstanceStatus;
import kh.edu.istad.stadoor.eureka.valueobject.LeasePolicy;
import kh.edu.istad.stadoor.eureka.valueobject.NetworkAddress;
import kh.edu.istad.stadoor.eureka.valueobject.ServiceInstanceId;
import kh.edu.istad.stadoor.eureka.entity.ServiceInstance;
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
    void shouldRejectLowercaseApplicationName() {
        ServiceInstance serviceInstance = ServiceInstance.builder()
                .id(new ServiceInstanceId("instance-1"))
                .applicationName(new ApplicationName("gateway-service"))
                .networkAddress(new NetworkAddress("gateway-1", "10.0.0.10", 8080, 8443, true))
                .healthCheckEndpoint(HealthCheckEndpoint.builder()
                        .healthUrl("http://gateway-1:8080/actuator/health")
                        .statusUrl("http://gateway-1:8080/actuator/info")
                        .homePageUrl("http://gateway-1:8080")
                        .build())
                .leasePolicy(new LeasePolicy(30, 90))
                .status(InstanceStatus.UP)
                .build();

        InvalidServiceRegistrationException exception = assertThrows(
                InvalidServiceRegistrationException.class,
                serviceInstance::validateForRegistration
        );

        assertEquals("application name must be uppercase and use letters, digits, '-' or '_'", exception.getMessage());
    }

    @Test
    void shouldRejectInvalidLeasePolicy() {
        ServiceInstance serviceInstance = ServiceInstance.builder()
                .id(new ServiceInstanceId("instance-1"))
                .applicationName(new ApplicationName("GATEWAY_SERVICE"))
                .networkAddress(new NetworkAddress("gateway-1", "10.0.0.10", 8080, 8443, true))
                .healthCheckEndpoint(HealthCheckEndpoint.builder()
                        .healthUrl("http://gateway-1:8080/actuator/health")
                        .statusUrl("http://gateway-1:8080/actuator/info")
                        .homePageUrl("http://gateway-1:8080")
                        .build())
                .leasePolicy(new LeasePolicy(30, 20))
                .status(InstanceStatus.UP)
                .build();

        InvalidServiceRegistrationException exception = assertThrows(
                InvalidServiceRegistrationException.class,
                serviceInstance::validateForRegistration
        );

        assertEquals("expiration duration must be greater than renewal interval", exception.getMessage());
    }

    @Test
    void shouldRejectDuplicateRegistration() {
        EurekaAggregate aggregate = new EurekaAggregate();
        ServiceInstance serviceInstance = validServiceInstance();

        aggregate.register(serviceInstance);

        InvalidServiceRegistrationException exception = assertThrows(
                InvalidServiceRegistrationException.class,
                () -> aggregate.register(validServiceInstance())
        );

        assertEquals("service instance id already registered", exception.getMessage());
    }

    private ServiceInstance validServiceInstance() {
        return ServiceInstance.builder()
                .id(new ServiceInstanceId("instance-1"))
                .applicationName(new ApplicationName("GATEWAY_SERVICE"))
                .networkAddress(new NetworkAddress("gateway-1", "10.0.0.10", 8080, 8443, true))
                .healthCheckEndpoint(HealthCheckEndpoint.builder()
                        .healthUrl("http://gateway-1:8080/actuator/health")
                        .statusUrl("http://gateway-1:8080/actuator/info")
                        .homePageUrl("http://gateway-1:8080")
                        .build())
                .leasePolicy(new LeasePolicy(30, 90))
                .status(InstanceStatus.UP)
                .build();
    }
}
