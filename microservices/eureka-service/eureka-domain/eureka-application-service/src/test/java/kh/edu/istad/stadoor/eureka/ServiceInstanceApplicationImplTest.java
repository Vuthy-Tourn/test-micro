package co.istad.stadoor.eureka;

import kh.edu.istad.stadoor.eureka.ServiceInstanceApplicationImpl;
import kh.edu.istad.stadoor.eureka.entity.ServiceInstance;
import kh.edu.istad.stadoor.eureka.mapper.ServiceInstanceDomainMapper;
import kh.edu.istad.stadoor.eureka.port.output.EurekaRegistrationPort;
import kh.edu.istad.stadoor.eureka.port.output.ServiceInstanceRepositoryPort;
import kh.edu.istad.stadoor.eureka.valueobject.ApplicationName;
import kh.edu.istad.stadoor.eureka.valueobject.HealthCheckEndpoint;
import kh.edu.istad.stadoor.eureka.valueobject.InstanceStatus;
import kh.edu.istad.stadoor.eureka.valueobject.LeasePolicy;
import kh.edu.istad.stadoor.eureka.valueobject.Metadata;
import kh.edu.istad.stadoor.eureka.valueobject.NetworkAddress;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceInstanceApplicationImplTest {

    @Mock
    private ServiceInstanceRepositoryPort serviceInstanceRepositoryPort;

    @Mock
    private EurekaRegistrationPort eurekaRegistrationPort;

    @Mock
    private ServiceInstanceDomainMapper serviceInstanceMapper;

    @InjectMocks
    private ServiceInstanceApplicationImpl serviceInstanceApplication;

    @Test
    void shouldRetryRenewalAndKeepInstanceWhenRenewalRecovers() {
        ServiceInstance serviceInstance = serviceInstance();
        AtomicInteger attempts = new AtomicInteger();

        when(serviceInstanceRepositoryPort.findAllInstances()).thenReturn(Flux.just(serviceInstance));
        when(eurekaRegistrationPort.renewLease(serviceInstance)).thenAnswer(invocation -> {
            if (attempts.incrementAndGet() < 3) {
                return Mono.error(new TimeoutException("renewal timed out"));
            }
            return Mono.empty();
        });

        serviceInstanceApplication.renewRegisteredServices();

        assertEquals(3, attempts.get());
        verify(serviceInstanceRepositoryPort, never()).save(any(ServiceInstance.class));
        verify(serviceInstanceRepositoryPort, never()).deleteById(any(String.class));
    }

    @Test
    void shouldMarkInstanceOutOfServiceAfterThreeFailedCycles() {
        ServiceInstance serviceInstance = serviceInstance();

        when(serviceInstanceRepositoryPort.findAllInstances()).thenReturn(Flux.just(serviceInstance));
        when(eurekaRegistrationPort.renewLease(serviceInstance))
                .thenReturn(Mono.error(new IllegalStateException("eureka unavailable")));
        when(serviceInstanceRepositoryPort.save(any(ServiceInstance.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        serviceInstanceApplication.renewRegisteredServices();
        serviceInstanceApplication.renewRegisteredServices();
        serviceInstanceApplication.renewRegisteredServices();

        ArgumentCaptor<ServiceInstance> captor = ArgumentCaptor.forClass(ServiceInstance.class);
        verify(serviceInstanceRepositoryPort, times(1)).save(captor.capture());
        assertEquals(InstanceStatus.OUT_OF_SERVICE, captor.getValue().getStatus());
        verify(serviceInstanceRepositoryPort, never()).deleteById(any(String.class));
    }

    @Test
    void shouldDeleteInstanceAfterFiveFailedCycles() {
        ServiceInstance serviceInstance = serviceInstance();

        when(serviceInstanceRepositoryPort.findAllInstances()).thenReturn(Flux.just(serviceInstance));
        when(eurekaRegistrationPort.renewLease(serviceInstance))
                .thenReturn(Mono.error(new IllegalStateException("eureka unavailable")));
        when(serviceInstanceRepositoryPort.save(any(ServiceInstance.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(serviceInstanceRepositoryPort.deleteById(serviceInstance.getUuid())).thenReturn(Mono.empty());

        for (int i = 0; i < 5; i++) {
            serviceInstanceApplication.renewRegisteredServices();
        }

        verify(serviceInstanceRepositoryPort, times(1)).save(any(ServiceInstance.class));
        verify(serviceInstanceRepositoryPort, times(1)).deleteById(serviceInstance.getUuid());
    }

    private ServiceInstance serviceInstance() {
        return ServiceInstance.builder()
                .uuid("instance-1")
                .applicationName(new ApplicationName("GATEWAY_SERVICE"))
                .networkAddress(new NetworkAddress("gateway-1", "10.0.0.10", 8080, 8443, true))
                .healthCheckEndpoint(HealthCheckEndpoint.builder()
                        .healthUrl("http://gateway-1:8080/actuator/health")
                        .statusUrl("http://gateway-1:8080/actuator/info")
                        .homePageUrl("http://gateway-1:8080")
                        .build())
                .leasePolicy(new LeasePolicy(30, 90))
                .status(InstanceStatus.UP)
                .metadata(new Metadata(Map.of("version", "1.0.0")))
                .build();
    }
}
