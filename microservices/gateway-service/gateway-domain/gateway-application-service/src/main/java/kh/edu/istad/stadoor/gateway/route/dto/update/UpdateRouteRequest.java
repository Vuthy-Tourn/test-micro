package kh.edu.istad.stadoor.gateway.route.dto.update;

import kh.edu.istad.stadoor.gateway.valueobject.route.HttpMethod;
import kh.edu.istad.stadoor.gateway.valueobject.route.RouteSecurity;

public record UpdateRouteRequest (
        String path,
        HttpMethod method,
        String targetPath,
        RouteSecurity routeSecurity
){

}
