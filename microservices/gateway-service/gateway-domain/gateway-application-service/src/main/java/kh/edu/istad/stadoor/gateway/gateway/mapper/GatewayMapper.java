package kh.edu.istad.stadoor.gateway.gateway.mapper;

import kh.edu.istad.stadoor.common.valueobject.TenantId;
import kh.edu.istad.stadoor.gateway.command.gateway.CreateGatewayCommand;
import kh.edu.istad.stadoor.gateway.command.gateway.UpdateGatewayCommand;
import kh.edu.istad.stadoor.gateway.gateway.dto.create.CreateGatewayRequest;
import kh.edu.istad.stadoor.gateway.gateway.dto.update.UpdateGatewayRequest;
import kh.edu.istad.stadoor.gateway.valueobject.gateway.GatewayDescription;
import kh.edu.istad.stadoor.common.valueobject.gateway.GatewayId;
import kh.edu.istad.stadoor.gateway.valueobject.gateway.GatewayName;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", imports = {TenantId.class, GatewayName.class, GatewayDescription.class})
public interface GatewayMapper {

    default CreateGatewayCommand createGatewayRequestToCreateGatewayCommand(
            GatewayId gatewayId,
            TenantId tenantId,
            CreateGatewayRequest createGatewayRequest
    ) {
        return new CreateGatewayCommand(
                gatewayId,
                tenantId,
                new GatewayName(createGatewayRequest.gatewayName()),
                new GatewayDescription(createGatewayRequest.description()),
                createGatewayRequest.status(),
                createGatewayRequest.authType(),
                createGatewayRequest.gatewayType()
        );
    }

    default UpdateGatewayCommand updateGatewayRequestToUpdateGatewayCommand(
            GatewayId gatewayId,
            UpdateGatewayRequest updateGatewayRequest
    ) {
        return new UpdateGatewayCommand(
                gatewayId,
                new GatewayName(updateGatewayRequest.gatewayName()),
                new GatewayDescription(updateGatewayRequest.description())
        );
    }
}
