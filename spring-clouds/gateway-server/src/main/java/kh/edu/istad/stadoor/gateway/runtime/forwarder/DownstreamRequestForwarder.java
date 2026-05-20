package kh.edu.istad.stadoor.gateway.runtime.forwarder;

import kh.edu.istad.stadoor.gateway.runtime.model.ConsumerContext;
import kh.edu.istad.stadoor.gateway.runtime.model.MatchedRoute;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface DownstreamRequestForwarder {

    Mono<ServerResponse> forward(ServerRequest request, MatchedRoute matchedRoute, ConsumerContext consumerContext);
}
