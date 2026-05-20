package kh.edu.istad.stadoor.gateway.route.mapper;

import kh.edu.istad.stadoor.gateway.entity.Route;
import kh.edu.istad.stadoor.gateway.route.entity.RouteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RouteEntityMapper {

    @Mapping(source = "routeId", target = "routeId.routeId")
    @Mapping(source = "gatewayId", target = "gatewayId.gatewayId")
    @Mapping(source = "serviceId", target = "serviceId.serviceId")
    @Mapping(source = "routePath", target = "routePath.routePath")
    @Mapping(source = "targetPath", target = "targetPath.targetPath")
    Route RouteEntityToRoute(RouteEntity routeEntity);
}
