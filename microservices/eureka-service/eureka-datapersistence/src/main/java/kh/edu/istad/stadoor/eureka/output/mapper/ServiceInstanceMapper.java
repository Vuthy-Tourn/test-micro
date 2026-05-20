package kh.edu.istad.stadoor.eureka.output.mapper;


import kh.edu.istad.stadoor.eureka.entity.ServiceInstance;
import kh.edu.istad.stadoor.eureka.valueobject.ApplicationName;
import kh.edu.istad.stdoor.eureka.entity.ServiceInstanceEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServiceInstanceMapper {

    // convert serviceInstance in core domain to serviceInstance in data persistence
    @Mapping(target = "id", source = "uuid")
    @Mapping(target = "applicationName", source = "applicationName.value")
    @Mapping(target = "instanceStatus", source = "status")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ServiceInstanceEntity fromServiceInstance(ServiceInstance serviceInstanceEntity);

    // Convert serviceInstance in data persistence to serviceInstance in core domain
    @Mapping(target = "uuid", source = "id")
    @Mapping(target = "applicationName", source = "applicationName")
    @Mapping(target = "status", source = "instanceStatus")
    ServiceInstance toServiceInstanceDomain(ServiceInstanceEntity serviceInstanceEntity);

    default ApplicationName toApplicationName(String applicationName) {
        // The domain value object centralizes application-name normalization.
        return applicationName == null ? null : new ApplicationName(applicationName);
    }
}
