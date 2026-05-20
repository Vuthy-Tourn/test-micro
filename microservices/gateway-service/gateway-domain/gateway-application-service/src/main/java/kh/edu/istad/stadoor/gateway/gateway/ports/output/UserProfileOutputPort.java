package kh.edu.istad.stadoor.gateway.gateway.ports.output;

import kh.edu.istad.stadoor.gateway.gateway.dto.query.UserProfilePageResponse;
import reactor.core.publisher.Mono;



public interface UserProfileOutputPort {

    Mono<UserProfilePageResponse> getUserProfile (String tenantId);

}
