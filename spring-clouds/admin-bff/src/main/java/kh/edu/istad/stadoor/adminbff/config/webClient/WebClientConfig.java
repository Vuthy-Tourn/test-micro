package kh.edu.istad.stadoor.adminbff.config.webClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfig {

    private WebClient buildWithTokenRelay(String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .filter((request, next) ->
                        // grab the current request from reactor context
                        Mono.deferContextual(ctx -> {
                            String token = ctx.getOrDefault("accessToken", null);
                            if (token != null) {
                                ClientRequest forwarded = ClientRequest.from(request)
                                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                        .build();
                                return next.exchange(forwarded);
                            }
                            return next.exchange(request);
                        })
                )
                .build();
    }

    @Bean("userProfileWebClient")
    public WebClient userProfileWebClient() {
        return buildWithTokenRelay("http://localhost:16802");
    }

    @Bean("gatewayClient")
    public WebClient gatewayWebClient() {
        return buildWithTokenRelay("http://localhost:16803");
    }

}
