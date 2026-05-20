package kh.edu.istad.stadoor.gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Component
@RequiredArgsConstructor
public class HttpInterfaceFactory {

    private final WebClient.Builder webClientBuilder;

    public <T> T createLoadBalanceClient(String baseUrl, Class<T> interfaceClass) {
        WebClient webClient = webClientBuilder
                .baseUrl(baseUrl)
                .filter((request, next) ->
                        ReactiveSecurityContextHolder.getContext()
                                .map(SecurityContext::getAuthentication)
                                .filter(auth -> auth instanceof JwtAuthenticationToken)
                                .cast(JwtAuthenticationToken.class)
                                .map(auth -> auth.getToken().getTokenValue())
                                .flatMap(token -> {
                                    ClientRequest newRequest = ClientRequest.from(request)
                                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                            .build();
                                    return next.exchange(newRequest);
                                })
                                .switchIfEmpty(next.exchange(request))
                )
                .build();

        return createClient(webClient, interfaceClass);
    }

    public <T> T createClient(WebClient webClient, Class<T> interfaceClass) {
        HttpServiceProxyFactory factory =
                HttpServiceProxyFactory
                        .builderFor(WebClientAdapter.create(webClient))
                        .build();
        return factory.createClient(interfaceClass);
    }
}