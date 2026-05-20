
package kh.edu.istad.stadoor.gateway.entity;

import kh.edu.istad.stadoor.common.valueobject.gateway.GatewayId;
import kh.edu.istad.stadoor.gateway.valueobject.route.*;
import kh.edu.istad.stadoor.gateway.valueobject.service.ServiceId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@NoArgsConstructor
public class Route {

    private RouteId routeId;
    private GatewayId gatewayId;
    private ServiceId serviceId;
    private RoutePath routePath;
    private TargetPath targetPath;
    private HttpMethod method;
    private RouteStatus status;
    private RouteSecurity secure;
    private Instant createdAt;
    private Instant updatedAt;

}
