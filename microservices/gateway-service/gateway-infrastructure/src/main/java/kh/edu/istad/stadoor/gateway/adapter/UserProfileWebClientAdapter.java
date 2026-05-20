package kh.edu.istad.stadoor.gateway.adapter;

import kh.edu.istad.stadoor.gateway.client.UserProfileClient;
import kh.edu.istad.stadoor.gateway.gateway.dto.query.UserProfilePageResponse;
import kh.edu.istad.stadoor.gateway.gateway.ports.output.UserProfileOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
public class UserProfileWebClientAdapter implements UserProfileOutputPort {

    private final UserProfileClient userProfileClient;
    @Override
    public Mono<UserProfilePageResponse> getUserProfile(String tenantId) {
            return userProfileClient.getUserProfile(tenantId);
    }
}
