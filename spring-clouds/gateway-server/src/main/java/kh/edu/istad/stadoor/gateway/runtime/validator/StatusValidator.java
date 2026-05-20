package kh.edu.istad.stadoor.gateway.runtime.validator;

import kh.edu.istad.stadoor.gateway.exception.InactiveGatewayException;
import kh.edu.istad.stadoor.gateway.exception.InactiveRouteException;
import kh.edu.istad.stadoor.gateway.exception.InactiveServiceException;
import kh.edu.istad.stadoor.gateway.runtime.model.MatchedRoute;
import org.springframework.stereotype.Component;

@Component
public class StatusValidator {

    public void validate(MatchedRoute matchedRoute) {
        if (!matchedRoute.gateway().active()) {
            throw new InactiveGatewayException("Gateway is inactive");
        }
        if (!matchedRoute.service().active()) {
            throw new InactiveServiceException("Service is inactive");
        }
        if (!matchedRoute.route().active()) {
            throw new InactiveRouteException("Route is inactive");
        }
    }
}
