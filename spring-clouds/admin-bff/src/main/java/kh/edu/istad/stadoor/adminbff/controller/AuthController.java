package kh.edu.istad.stadoor.adminbff.controller;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/api/token")
    public Mono<String> getAccessToken(
            @RegisteredOAuth2AuthorizedClient("stadoor-admin-bff") OAuth2AuthorizedClient client) {
        return Mono.just(client.getAccessToken().getTokenValue());
    }

}
