package kh.edu.istad.stadoor.gateway.admin.port.output;

import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;

public interface GlobalOverviewQueryOutputPort {

    Mono<Long> countAllGateways();

    Mono<Long> countAllServices();

    Mono<Long> countAllRoutes();

//    Mono<Long> countGatewaysBefore(LocalDateTime dateTime);
//    Mono<Long> countServicesBefore(LocalDateTime dateTime);
//    Mono<Long> countRoutesBefore(LocalDateTime dateTime);

    Mono<Long> countGatewaysBetween(Instant from, Instant to);
    Mono<Long> countServicesBetween(Instant from, Instant to);
    Mono<Long> countRoutesBetween(Instant from, Instant to);

}
