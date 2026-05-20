package kh.edu.istad.stadoor.gateway.client;

import kh.edu.istad.stadoor.gateway.gateway.dto.query.UserProfilePageResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import reactor.core.publisher.Mono;

@HttpExchange("/api/user-profile")
public interface UserProfileClient {

    @GetExchange("/tenant/{id}")
    Mono<UserProfilePageResponse> getUserProfile(@PathVariable String id);
}
