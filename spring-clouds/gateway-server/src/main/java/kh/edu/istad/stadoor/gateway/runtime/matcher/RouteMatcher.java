package kh.edu.istad.stadoor.gateway.runtime.matcher;

import kh.edu.istad.stadoor.gateway.runtime.model.GatewayRuntimeConfig;
import kh.edu.istad.stadoor.gateway.runtime.model.MatchedRoute;
import org.springframework.http.HttpMethod;

import java.util.Optional;

public interface RouteMatcher {

    Optional<MatchedRoute> match(GatewayRuntimeConfig config, String path, HttpMethod method);
}
