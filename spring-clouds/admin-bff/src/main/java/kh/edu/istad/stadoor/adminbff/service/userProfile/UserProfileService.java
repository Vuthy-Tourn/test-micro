package kh.edu.istad.stadoor.adminbff.service.userProfile;

import kh.edu.istad.stadoor.adminbff.dto.UserProfileWithGatewayResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface UserProfileService {

    Mono<UserProfileWithGatewayResponse> getUserProfileWithGateway(String userId, ServerWebExchange exchange);

}
