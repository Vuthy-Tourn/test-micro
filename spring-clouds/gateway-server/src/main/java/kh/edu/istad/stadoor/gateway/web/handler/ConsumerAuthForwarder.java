package kh.edu.istad.stadoor.gateway.web.handler;

import kh.edu.istad.stadoor.gateway.runtime.model.ResolvedRuntimeConfig;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface ConsumerAuthForwarder {
    Mono<ServerResponse> forward(ServerRequest request, ResolvedRuntimeConfig config);
}
