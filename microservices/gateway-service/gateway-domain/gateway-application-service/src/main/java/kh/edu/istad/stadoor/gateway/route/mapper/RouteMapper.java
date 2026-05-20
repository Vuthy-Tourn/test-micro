package kh.edu.istad.stadoor.gateway.route.mapper;

import kh.edu.istad.stadoor.gateway.command.route.CreateRouteCommand;
import kh.edu.istad.stadoor.gateway.entity.Route;
import kh.edu.istad.stadoor.gateway.route.dto.CreateRouteRequest;
import kh.edu.istad.stadoor.gateway.route.dto.RouteResponse;
import kh.edu.istad.stadoor.common.valueobject.gateway.GatewayId;
import kh.edu.istad.stadoor.gateway.valueobject.route.RouteId;
import kh.edu.istad.stadoor.gateway.valueobject.route.RoutePath;
import kh.edu.istad.stadoor.gateway.valueobject.route.TargetPath;
import kh.edu.istad.stadoor.gateway.valueobject.service.ServiceId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface RouteMapper {

    @Mapping(target = "routeId", source = "routeId")
    @Mapping(target = "gatewayId", source = "createRouteRequest.gatewayId")
    @Mapping(target = "serviceId", source = "createRouteRequest.serviceId")
    @Mapping(target = "path", source = "createRouteRequest.path")
    @Mapping(target = "targetPath", source = "createRouteRequest.targetPath")
    CreateRouteCommand createRouteRequestToCreateRouteCommand(RouteId routeId, CreateRouteRequest createRouteRequest);

    @Mapping(target = "routeId", source = "routeId.routeId")
    @Mapping(target = "gatewayId", source = "gatewayId.gatewayId")
    @Mapping(target = "serviceId", source = "serviceId.serviceId")
    @Mapping(target = "path", source = "routePath.routePath")
    @Mapping(target = "targetPath", source = "targetPath.targetPath")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "routeSecurity", source = "secure")
    RouteResponse RouteToRouteResponse(Route route);

    default RouteId mapRouteId(UUID id) {
        return new RouteId(id);
    }

    default GatewayId mapGatewayId(UUID id) {
        return new GatewayId(id);
    }

    default ServiceId mapServiceId(UUID id) {
        return new ServiceId(id);
    }

    default RoutePath mapRoutePath(String path) {
        return new RoutePath(path);
    }

    default TargetPath mapTargetPath(String path) {
        return new TargetPath(path);
    }
}