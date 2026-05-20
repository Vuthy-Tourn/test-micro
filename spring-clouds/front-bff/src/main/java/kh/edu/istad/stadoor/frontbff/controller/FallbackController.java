package kh.edu.istad.stadoor.frontbff.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    private Mono<ResponseEntity<Map<String, Object>>> unavailable(String message, String path) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        body.put("error", "Service Unavailable");
        body.put("message", message);
        body.put("path", path);

        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body));
    }

    @GetMapping("/gateway-static")
    public Mono<ResponseEntity<Map<String, Object>>> gatewayStaticFallback() {
        return unavailable(
                "gateway frontend static assets are temporarily unavailable. please try retry again",
                "/gateway-static/**"
        );
    }

    @GetMapping("/iam-static")
    public Mono<ResponseEntity<Map<String, Object>>> iamStaticFallback() {
        return unavailable(
                "iam frontend static assets are temporarily unavailable. please try retry again",
                "/iam-static/**"
        );
    }

    @GetMapping("/gateway-favicon")
    public Mono<ResponseEntity<Map<String, Object>>> gatewayFaviconFallback() {
        return unavailable(
                "gateway favicon is temporarily unavailable. please try retry again",
                "/favicon.ico"
        );
    }

    @GetMapping("/public-next")
    public Mono<ResponseEntity<Map<String, Object>>> publicNextFallback() {
        return unavailable(
                "public frontend runtime assets are temporarily unavailable. please try retry again",
                "/_next/**"
        );
    }

    @GetMapping("/public-images")
    public Mono<ResponseEntity<Map<String, Object>>> publicImagesFallback() {
        return unavailable(
                "public frontend images are temporarily unavailable. please try retry again",
                "/images/**"
        );
    }

    @GetMapping("/iam-dashboard")
    public Mono<ResponseEntity<Map<String, Object>>> iamDashboardFallback() {
        return unavailable(
                "iam dashboard is temporarily unavailable. please try retry again",
                "/dashboard/iam/**"
        );
    }

    @GetMapping("/gateway-dashboard")
    public Mono<ResponseEntity<Map<String, Object>>> gatewayDashboardFallback() {
        return unavailable(
                "gateway dashboard is temporarily unavailable. please try retry again",
                "/dashboard/**"
        );
    }

    @GetMapping("/public")
    public Mono<ResponseEntity<Map<String, Object>>> publicFallback() {
        return unavailable(
                "public frontend is temporarily unavailable. please try retry again",
                "/,/about,/contact,/login"
        );
    }

    @GetMapping("/api/me")
    public Mono<ResponseEntity<Map<String, Object>>> meFallback() {
        return unavailable(
                "profile service is temporarily unavailable. please try retry again",
                "/api/front/me"
        );
    }

    @GetMapping("/api/subscription")
    public Mono<ResponseEntity<Map<String, Object>>> subscriptionFallback() {
        return unavailable(
                "subscription service is temporarily unavailable. please try retry again",
                "/api/front/subscription"
        );
    }

    @GetMapping("/api/notifications")
    public Mono<ResponseEntity<Map<String, Object>>> notificationsFallback() {
        return unavailable(
                "notification service is temporarily unavailable. please try retry again",
                "/api/front/notifications"
        );
    }

    @GetMapping("/api/register")
    public Mono<ResponseEntity<Map<String, Object>>> registerFallback() {
        return unavailable(
                "registration service is temporarily unavailable. please try retry again",
                "/api/front/register"
        );
    }
}
