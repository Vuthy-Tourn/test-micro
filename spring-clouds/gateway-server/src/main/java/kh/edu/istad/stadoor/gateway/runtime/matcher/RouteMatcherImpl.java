package kh.edu.istad.stadoor.gateway.runtime.matcher;

import kh.edu.istad.stadoor.gateway.runtime.model.GatewayRuntimeConfig;
import kh.edu.istad.stadoor.gateway.runtime.model.MatchedRoute;
import kh.edu.istad.stadoor.gateway.runtime.model.RouteRuntimeConfig;
import kh.edu.istad.stadoor.gateway.runtime.model.ServiceRuntimeConfig;
import kh.edu.istad.stadoor.gateway.util.PathUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class RouteMatcherImpl implements RouteMatcher {

    @Override
    public Optional<MatchedRoute> match(GatewayRuntimeConfig config, String path, HttpMethod method) {
        for (ServiceRuntimeConfig service : config.services()) {
            for (RouteRuntimeConfig route : service.routes()) {
                boolean methodMatched = method.name().equalsIgnoreCase(route.method());
                boolean pathMatched = PathUtils.matches(route.path(), path);
                if (methodMatched && pathMatched) {
                    log.info("Matched route: routeId={}, routePath={}, targetPath={}, serviceId={}, serviceName={}",
                            route.routeId(), route.path(), route.targetPath(), service.serviceId(), service.name());
                    return Optional.of(new MatchedRoute(config, service, route));
                }
            }
        }
        log.warn("No route matched: method={}, path={}", method, path);
        return Optional.empty();
    }
}
