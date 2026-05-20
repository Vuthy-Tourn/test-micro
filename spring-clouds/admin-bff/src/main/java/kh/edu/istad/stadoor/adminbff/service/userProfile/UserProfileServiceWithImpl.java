package kh.edu.istad.stadoor.adminbff.service.userProfile;

import kh.edu.istad.stadoor.adminbff.dto.GatewayResponse;
import kh.edu.istad.stadoor.adminbff.dto.UserProfileResponse;
import kh.edu.istad.stadoor.adminbff.dto.UserProfileWithGatewayResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileServiceWithImpl implements UserProfileService {

    @Qualifier("userProfileWebClient")
    private final WebClient userProfileWebClient;

    @Qualifier("gatewayClient")
    private final WebClient gatewayClient;

    private final ReactiveOAuth2AuthorizedClientService authorizedClientService;

    @Override
    public Mono<UserProfileWithGatewayResponse> getUserProfileWithGateway(String userId, ServerWebExchange exchange) {

        return getAccessToken()
                .doOnNext(token -> log.info("Token retrieved successfully, length={}", token.length()))
                .doOnError(e -> log.error("Failed to get token: {}", e.getMessage()))
                .flatMap(token ->
                        userProfileWebClient.get()
                                .uri("/api/user-profile/{id}", userId)
                                .headers(h -> h.setBearerAuth(token))
                                .retrieve()
                                .onStatus(HttpStatusCode::isError, response ->
                                        response.bodyToMono(String.class)
                                                .flatMap(err -> Mono.error(new RuntimeException("User profile fetch failed: " + err)))
                                )
                                .bodyToMono(UserProfileResponse.class)
                                .doOnNext(user -> log.info("Got user, tenantId={}", user.tenantId()))
                                .flatMap(user ->
                                        fetchGatewaysByTenantId(user.tenantId(), token)
                                                .collectList()
                                                .doOnNext(list -> log.info("Got {} gateways", list.size()))
                                                .map(gateways -> toResponse(user, gateways))
                                )
                );
    }

    // Extract access token from the reactive security context (session-based OAuth2)
    private Mono<String> getAccessToken() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> (OAuth2AuthenticationToken) ctx.getAuthentication())
                .flatMap(auth -> authorizedClientService.loadAuthorizedClient(
                        auth.getAuthorizedClientRegistrationId(),
                        auth.getName()
                ))
                .cast(OAuth2AuthorizedClient.class)                      // ← explicit cast
                .map(client -> client.getAccessToken().getTokenValue());
    }

    private Flux<UserProfileWithGatewayResponse.GatewayInfo> fetchGatewaysByTenantId(String tenantId, String token) {
        log.info("Fetching gateways for tenantId={}", tenantId);

        return gatewayClient.get()
                .uri("/api/v1/gateways/tenant/{tenantId}", tenantId)
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class).flatMap(error -> {
                            log.error("Gateway service returned error: {}", error);
                            return Mono.error(new RuntimeException("Gateways fetch failed: " + error));
                        })
                )
                .bodyToFlux(GatewayResponse.class)
                .doOnNext(gw -> log.info("Raw gateway: {}", gw))
                .map(gw -> new UserProfileWithGatewayResponse.GatewayInfo(gw.gatewayName()))
                .onErrorResume(er -> {
                    log.error("Failed to fetch gateways for tenant={}: {}", tenantId, er.getMessage(), er);
                    return Flux.empty();
                });
    }

    private UserProfileWithGatewayResponse toResponse(
            UserProfileResponse user,
            List<UserProfileWithGatewayResponse.GatewayInfo> gateways) {
        return new UserProfileWithGatewayResponse(
                user.id(),
                user.username(),
                user.email(),
                user.roleId(),
                user.profileImage(),
                user.clientIds(),
                user.tenantId(),
                gateways
        );
    }
}