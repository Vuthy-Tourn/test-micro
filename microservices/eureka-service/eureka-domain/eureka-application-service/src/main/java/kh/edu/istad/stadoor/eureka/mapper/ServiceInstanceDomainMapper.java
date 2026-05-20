package kh.edu.istad.stadoor.eureka.mapper;

import kh.edu.istad.stdoor.eureka.dto.ServiceInstanceCreateRequest;
import kh.edu.istad.stadoor.eureka.entity.ServiceInstance;
import kh.edu.istad.stadoor.eureka.valueobject.ApplicationName;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServiceInstanceDomainMapper {

    @Mapping(target = "uuid", source = "instanceServiceId")
    @Mapping(target = "applicationName", source = "applicationName")
    @Mapping(target = "status", source = "instanceStatus")
    ServiceInstance toServiceInstanceDomain(ServiceInstanceCreateRequest request);

    default ApplicationName toApplicationName(String applicationName) {
        // The domain value object applies the shared normalization rules for every caller.
        return applicationName == null ? null : new ApplicationName(applicationName);
    }


}
