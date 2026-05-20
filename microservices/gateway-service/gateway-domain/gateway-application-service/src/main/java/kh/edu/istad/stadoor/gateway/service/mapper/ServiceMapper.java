package kh.edu.istad.stadoor.gateway.service.mapper;

import kh.edu.istad.stadoor.gateway.command.service.RegisterServiceCommand;
import kh.edu.istad.stadoor.gateway.command.service.UpdateServiceCommand;
import kh.edu.istad.stadoor.gateway.entity.Service;
import kh.edu.istad.stadoor.gateway.service.dto.ServiceResponse;
import kh.edu.istad.stadoor.common.valueobject.gateway.GatewayId;
import kh.edu.istad.stadoor.gateway.valueobject.service.BaseUrl;
import kh.edu.istad.stadoor.gateway.valueobject.service.ServiceId;
import kh.edu.istad.stadoor.gateway.service.dto.register.RegisterServiceRequest;
import kh.edu.istad.stadoor.gateway.service.dto.update.UpdateServiceRequest;
import kh.edu.istad.stadoor.gateway.valueobject.service.ServiceName;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ServiceMapper {

    @Mapping(target = "serviceId",  source = "serviceId")
//    @Mapping(target= "status", source = "registerServiceRequest.status")
    RegisterServiceCommand registerServiceRequestToRegisterServiceCommand(
            ServiceId serviceId,
            RegisterServiceRequest registerServiceRequest
    );

    @Mapping(target = "serviceId.serviceId", source = "serviceId")
//    @Mapping(target = "name", source = "updateServiceRequest.serviceName")
//    @Mapping(target = "baseUrl", source = "updateServiceRequest.baseUrl")
    UpdateServiceCommand updateServiceRequestToUpdateServiceCommand(UUID serviceId, UpdateServiceRequest updateServiceRequest);


    @Mapping(target = "serviceId", source = "serviceId.serviceId")
    @Mapping(target = "gatewayId", source = "gatewayId.gatewayId")
    @Mapping(target = "baseUrl", source = "baseUrl.baseUrl")
    @Mapping(target = "name", source = "serviceName.name")
    @Mapping(target = "type", source = "serviceType")
    @Mapping(target = "gatewayName", ignore = true)
    ServiceResponse serviceToServiceResponse(Service service);
//    ServicesEntry serviceRegistedEventToServicesEntry(ServiceRegisteredEvent serviceRegisteredEvent);

    default ServiceResponse serviceToServiceResponse(Service service, String gatewayName) {
        ServiceResponse base = serviceToServiceResponse(service);
        return new ServiceResponse(
                base.serviceId(),
                base.gatewayId(),
                gatewayName,
                base.name(),
                base.type(),
                base.baseUrl(),
                base.status(),
                base.createdAt()
        );
    }

    default GatewayId toGatewayId(UUID value) {
        return new GatewayId(value);
    }

    default ServiceName toServiceName(String value) {
        return new ServiceName(value);
    }

    default BaseUrl toBaseUrl(String value) {
        return new BaseUrl(value);
    }
}