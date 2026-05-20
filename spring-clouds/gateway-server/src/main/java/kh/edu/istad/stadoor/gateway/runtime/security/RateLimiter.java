package kh.edu.istad.stadoor.gateway.runtime.security;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;

@Component
public class RateLimiter {

    private static final int MAX_REQUESTS_PER_MINUTE = 60;

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    public RateLimiter(ReactiveRedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Mono<Boolean> isAllowed(UUID consumerId) {
        String key = "rate:" + consumerId.toString();
        return redisTemplate.opsForValue()
                .increment(key)
                .flatMap(count -> {
                    if (count == 1) {
                        return redisTemplate.expire(key, Duration.ofMinutes(1))
                                .thenReturn(true);
                    }
                return Mono.just(count <= MAX_REQUESTS_PER_MINUTE);
                });
    }
}
