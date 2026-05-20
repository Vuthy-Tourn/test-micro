package kh.edu.istad.stadoor.gateway.service.mapper;

import kh.edu.istad.stadoor.gateway.entity.Service;
import kh.edu.istad.stadoor.gateway.service.entity.ServiceEntity;
import kh.edu.istad.stadoor.gateway.valueobject.service.ServiceStatus;
import kh.edu.istad.stadoor.gateway.valueobject.service.ServiceType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServiceDataAccessMapper {

                @Mapping(target = "serviceId", source = "serviceId.serviceId")
                @Mapping(target = "gatewayId", source = "gatewayId.gatewayId")
                @Mapping(target = "name", source = "serviceName.name")
                @Mapping(target = "serviceType", source = "serviceType")
                @Mapping(target = "status", source = "status")
                @Mapping(target = "baseUrl", source = "baseUrl.baseUrl")
                @Mapping(target = "createdAt", source = "createdAt")
                ServiceEntity serviceToServiceEntity(Service service);


                @Mapping(target = "serviceId", expression = "java(new ServiceId(entity.getServiceId()))")
                @Mapping(target = "gatewayId", expression = "java(new GatewayId(entity.getGatewayId()))")
                @Mapping(target = "serviceName", expression = "java(new ServiceName(entity.getName()))")
                @Mapping(target = "baseUrl", expression = "java(new BaseUrl(entity.getBaseUrl()))")
                @Mapping(target = "createdAt", source = "createdAt")
                @Mapping(target = "updatedAt", source = "updatedAt")
                Service serviceEntityToService(ServiceEntity entity);


                // Enum → String
                default String map(ServiceType type) {
                        return type == null ? null : type.name();
                }

                default String map(ServiceStatus status) {
                        return status == null ? null : status.name();
                }

                // String → Enum
                default ServiceType mapServiceType(String type) {
                        return type == null ? null : ServiceType.valueOf(type);
                }

                default ServiceStatus mapServiceStatus(String status) {
                        return status == null ? null : ServiceStatus.valueOf(status);
                }

}
