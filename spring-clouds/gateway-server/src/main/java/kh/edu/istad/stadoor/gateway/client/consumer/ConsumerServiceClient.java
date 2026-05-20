package kh.edu.istad.stadoor.gateway.client.consumer;

import kh.edu.istad.stadoor.gateway.client.consumer.dto.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Component
public class ConsumerServiceClient {

    private static final String VALIDATE_PATH = "/api/v1/consumers/validate";

    private final WebClient webClient;

    public ConsumerServiceClient(
            @Qualifier("defaultWebClientBuilder") WebClient.Builder webClientBuilder,
            @Value("${stadoor.clients.consumer.base-url:http://localhost:16804}") String baseUrl
    ) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public Mono<ApiKeyValidationResponse> validateApiKey(String apiKey, UUID gatewayId) {
        ValidateConsumerCredentialRequest request = new ValidateConsumerCredentialRequest(
                "API_KEY",
                null,
                null,
                apiKey,
                null,
                null,
                gatewayId
        );

        return webClient.post()
                .uri(VALIDATE_PATH)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<ApiKeyValidationResponse>>() {})
                .map(ApiResponse::data);
    }

    public Mono<BasicAuthValidationResponse> validateBasicAuth(String basicHeader, UUID gatewayId) {
        BasicCredentials credentials = decodeBasicCredentials(basicHeader);

        ValidateConsumerCredentialRequest request = new ValidateConsumerCredentialRequest(
                "BASIC_AUTH",
                credentials.username,
                credentials.password,
                null,
                null,
                null,
                gatewayId
        );

        return webClient.post()
                .uri(VALIDATE_PATH)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<BasicAuthValidationResponse>>() {})
                .map(ApiResponse::data);
    }

    public Mono<JwtValidationResponse> validateJwt(String bearerToken, UUID gatewayId) {
        ValidateConsumerCredentialRequest request = new ValidateConsumerCredentialRequest(
                "JWT",
                null,
                null,
                null,
                stripBearerPrefix(bearerToken),
                null,
                gatewayId
        );

        return webClient.post()
                .uri(VALIDATE_PATH)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<JwtValidationResponse>>() {})
                .map(ApiResponse::data);
    }

    private BasicCredentials decodeBasicCredentials(String authorization) {
        if (authorization == null || !authorization.regionMatches(true, 0, "Basic ", 0, 6)) {
            throw new IllegalArgumentException("Authorization header must use Basic scheme");
        }

        String encoded = authorization.substring(6);
        String decoded = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
        int separatorIndex = decoded.indexOf(":");

        if (separatorIndex < 0) {
            throw new IllegalArgumentException("Invalid Basic Authorization header");
        }

        return new BasicCredentials(
                decoded.substring(0, separatorIndex),
                decoded.substring(separatorIndex + 1)
        );
    }

    private record BasicCredentials(String username, String password) {
    }

    private String stripBearerPrefix(String authorization) {
        if (authorization == null) {
            return null;
        }

        if (authorization.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return authorization.substring(7);
        }

        return authorization;
    }

}
