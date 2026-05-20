package kh.edu.istad.stadoor.gateway.runtime.security;

import kh.edu.istad.stadoor.gateway.client.consumer.ConsumerServiceClient;
import kh.edu.istad.stadoor.gateway.client.consumer.dto.ApiKeyValidationResponse;
import kh.edu.istad.stadoor.gateway.exception.UnauthorizedRequestException;
import kh.edu.istad.stadoor.gateway.runtime.model.ConsumerContext;
import kh.edu.istad.stadoor.gateway.runtime.model.MatchedRoute;
import kh.edu.istad.stadoor.gateway.util.HeaderUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@Component
public class ApiKeyAuthValidator {

    private final ConsumerServiceClient consumerServiceClient;

    public ApiKeyAuthValidator(ConsumerServiceClient consumerServiceClient) {
        this.consumerServiceClient = consumerServiceClient;
    }

    public Mono<ConsumerContext> validate(ServerRequest request, MatchedRoute matchedRoute) {
        String apiKey = HeaderUtils.getHeader(request, "X-Api-Key")
                .orElseThrow(() -> new UnauthorizedRequestException("Missing X-Api-Key header"));

        return consumerServiceClient.validateApiKey(apiKey, matchedRoute.gateway().gatewayId())
                .filter(ApiKeyValidationResponse::valid)
                .switchIfEmpty(Mono.error(new UnauthorizedRequestException("Invalid API key")))
                .map(response -> new ConsumerContext(
                        response.consumerId(),
                        response.tenantId(),
                        response.gatewayId(),
                        response.roles()
                ));
    }
}
