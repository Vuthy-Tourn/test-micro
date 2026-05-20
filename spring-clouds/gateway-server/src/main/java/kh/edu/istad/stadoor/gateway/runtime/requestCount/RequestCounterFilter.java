package kh.edu.istad.stadoor.gateway.runtime.requestCount;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Order(1)
@Component
@RequiredArgsConstructor
public class RequestCounterFilter implements WebFilter {

    private final RequestCounterService counterService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        // skip counting internal actuator calls
        if (path.startsWith("/actuator")) {
            return chain.filter(exchange);
        }

        return counterService.increment()
                .doOnError(e -> log.error("Failed to increment request counter: {}", e.getMessage()))
                .onErrorComplete()   // Redis down → gateway keeps working
                .then(chain.filter(exchange));
    }
}
