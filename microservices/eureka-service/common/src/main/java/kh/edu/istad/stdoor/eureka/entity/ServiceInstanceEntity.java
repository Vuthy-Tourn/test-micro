package kh.edu.istad.stdoor.eureka.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "service_instance")
public class ServiceInstanceEntity {

    @Id
    private String id;

    @Field("application_name")
    private String applicationName;

    @Field("instance_status")
    private InstanceStatus instanceStatus;

    @Field("network_address")
    private NetworkAddress networkAddress;

    @Field("lease_policy")
    private LeasePolicy leasePolicy;

    @Field("health_check_endpoint")
    private HealthCheckEndpoint healthCheckEndpoint;

    @Field("metadata")
    private Metadata metadata;


    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;
}
