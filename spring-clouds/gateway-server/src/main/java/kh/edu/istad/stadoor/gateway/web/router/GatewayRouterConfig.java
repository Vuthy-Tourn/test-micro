package kh.edu.istad.stadoor.gateway.web.router;

import kh.edu.istad.stadoor.gateway.web.handler.GatewayRequestHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;

@Configuration
public class GatewayRouterConfig {

    @Bean
    RouterFunction<ServerResponse> gatewayRoutes(GatewayRequestHandler gatewayRequestHandler) {
//        return route(all(), gatewayRequestHandler::handle);

        return route(
                path("/actuator/**").negate(),
                gatewayRequestHandler::handle
        );
    }
}
