package kh.edu.istad.stadoor.gateway.runtime.validator;

import kh.edu.istad.stadoor.gateway.runtime.model.MatchedRoute;
import org.springframework.stereotype.Component;

@Component
public class RouteSecurityValidator {

    public boolean requiresValidation(MatchedRoute matchedRoute) {
        return matchedRoute.route().secured();
    }
}
