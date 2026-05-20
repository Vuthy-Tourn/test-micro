package kh.edu.istad.stadoor.gateway.util;

import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Optional;

public final class HeaderUtils {

    private HeaderUtils() {
    }

    public static Optional<String> getHeader(ServerRequest request, String name) {
        return Optional.ofNullable(request.headers().firstHeader(name))
                .filter(value -> !value.isBlank());
    }
}
