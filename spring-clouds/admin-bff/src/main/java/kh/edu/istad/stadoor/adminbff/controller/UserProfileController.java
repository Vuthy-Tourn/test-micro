package kh.edu.istad.stadoor.adminbff.controller;

import kh.edu.istad.stadoor.adminbff.dto.UserProfileWithGatewayResponse;
import kh.edu.istad.stadoor.adminbff.service.userProfile.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileBffService;

    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserProfileWithGatewayResponse>> getUserWithGateways(
            @PathVariable String id,
            ServerWebExchange exchange) {         // ✅ inject exchange here
        return userProfileBffService.getUserProfileWithGateway(id, exchange)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}