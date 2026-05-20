package kh.edu.istad.stadoor.gateway.runtime.resolver;

import kh.edu.istad.stadoor.gateway.runtime.model.ResolvedRuntimeConfig;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

public interface RuntimeConfigResolver {

    Mono<ResolvedRuntimeConfig> resolve(ServerRequest request);
}
