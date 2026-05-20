package kh.edu.istad.stdoor.eureka.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ServiceInstanceCreateRequest{

    private String applicationName;
    private String instanceServiceId;
    private NetworkAddress networkAddress;
    private LeasePolicy leasePolicy;
    private HealthCheckEndpoint healthCheckEndpoint;
    private InstanceStatus instanceStatus;
    private Metadata metadata;

}
