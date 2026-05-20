package kh.edu.istad.stadoor.gateway.runtime.security;

import kh.edu.istad.stadoor.gateway.client.consumer.ConsumerServiceClient;
import kh.edu.istad.stadoor.gateway.client.consumer.dto.JwtValidationResponse;
import kh.edu.istad.stadoor.gateway.exception.UnauthorizedRequestException;
import kh.edu.istad.stadoor.gateway.runtime.model.ConsumerContext;
import kh.edu.istad.stadoor.gateway.runtime.model.MatchedRoute;
import kh.edu.istad.stadoor.gateway.util.HeaderUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthValidator {

    private final ConsumerServiceClient consumerServiceClient;

    public JwtAuthValidator(ConsumerServiceClient consumerServiceClient) {
        this.consumerServiceClient = consumerServiceClient;
    }

    public Mono<ConsumerContext> validate(ServerRequest request, MatchedRoute matchedRoute) {
        String authorization = HeaderUtils.getHeader(request, "Authorization")
                .orElseThrow(() -> new UnauthorizedRequestException("Missing Authorization header"));

        return consumerServiceClient.validateJwt(authorization, matchedRoute.gateway().gatewayId())
                .filter(JwtValidationResponse::valid)
                .switchIfEmpty(Mono.error(new UnauthorizedRequestException("Invalid JWT token")))
                .map(response -> new ConsumerContext(
                        response.consumerId(),
                        response.tenantId(),
                        response.gatewayId(),
                        response.roles()
                ));
    }
}
