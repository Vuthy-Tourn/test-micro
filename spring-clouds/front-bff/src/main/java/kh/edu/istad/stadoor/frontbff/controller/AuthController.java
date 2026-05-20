package kh.edu.istad.stadoor.frontbff.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final ReactiveOAuth2AuthorizedClientManager authorizedClientManager;

    public AuthController(ReactiveOAuth2AuthorizedClientManager authorizedClientManager) {
        this.authorizedClientManager = authorizedClientManager;
    }

    @GetMapping("/api/token")
    public Mono<String> getAccessToken(
            @RegisteredOAuth2AuthorizedClient("stadoor") OAuth2AuthorizedClient client) {
        return Mono.just(client.getAccessToken().getTokenValue());
    }

    @GetMapping("/is-authenticated")
    public Mono<ResponseEntity<Void>> isAuthenticated(
            Authentication authentication,
            ServerWebExchange exchange
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Mono.just(ResponseEntity.status(401).build());
        }

        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId("stadoor")
                .principal(authentication)
                .attribute(ServerWebExchange.class.getName(), exchange)
                .build();

        return authorizedClientManager.authorize(authorizeRequest)
                .map(authorizedClient -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.status(401).build())
                .onErrorReturn(ResponseEntity.status(401).build());
    }
}
