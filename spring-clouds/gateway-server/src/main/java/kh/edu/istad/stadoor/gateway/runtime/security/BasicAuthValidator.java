package kh.edu.istad.stadoor.gateway.runtime.security;

import kh.edu.istad.stadoor.gateway.client.consumer.ConsumerServiceClient;
import kh.edu.istad.stadoor.gateway.client.consumer.dto.BasicAuthValidationResponse;
import kh.edu.istad.stadoor.gateway.exception.UnauthorizedRequestException;
import kh.edu.istad.stadoor.gateway.runtime.model.ConsumerContext;
import kh.edu.istad.stadoor.gateway.runtime.model.MatchedRoute;
import kh.edu.istad.stadoor.gateway.util.HeaderUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@Component
public class BasicAuthValidator {

    private final ConsumerServiceClient consumerServiceClient;

    public BasicAuthValidator(ConsumerServiceClient consumerServiceClient) {
        this.consumerServiceClient = consumerServiceClient;
    }

    public Mono<ConsumerContext> validate(ServerRequest request, MatchedRoute matchedRoute) {
        String authorization = HeaderUtils.getHeader(request, "Authorization")
                .orElseThrow(() -> new UnauthorizedRequestException("Missing Authorization header"));

        return consumerServiceClient.validateBasicAuth(authorization, matchedRoute.gateway().gatewayId())
                .filter(BasicAuthValidationResponse::valid)
                .switchIfEmpty(Mono.error(new UnauthorizedRequestException("Invalid basic credentials")))
                .map(response -> new ConsumerContext(
                        response.consumerId(),
                        response.tenantId(),
                        response.gatewayId(),
                        response.roles()
                ));
    }
}
