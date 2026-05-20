package kh.edu.istad.stadoor.gateway.client.iam;

import kh.edu.istad.stadoor.gateway.client.iam.dto.TokenValidationResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class IamServiceClient {

    private final WebClient webClient;

    public IamServiceClient(
            @Qualifier("defaultWebClientBuilder") WebClient.Builder webClientBuilder,
            @Value("${stadoor.clients.iam.base-url:http://localhost:9090}") String baseUrl
    ) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public Mono<TokenValidationResponse> validateToken(String bearerToken) {
        return webClient.post()
                .uri("/api/runtime/tokens/validate")
                .header("Authorization", bearerToken)
                .retrieve()
                .bodyToMono(TokenValidationResponse.class);
    }
}
