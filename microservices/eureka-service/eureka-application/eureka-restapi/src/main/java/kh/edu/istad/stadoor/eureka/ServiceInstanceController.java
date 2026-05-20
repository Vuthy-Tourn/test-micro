package kh.edu.istad.stadoor.eureka;


import kh.edu.istad.stadoor.eureka.entity.ServiceInstance;
import kh.edu.istad.stadoor.eureka.port.input.ServiceInstancePort;
import kh.edu.istad.stdoor.eureka.dto.ServiceInstanceCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/service-instances")
@RequiredArgsConstructor
public class ServiceInstanceController {

    private final ServiceInstancePort serviceInstancePort;

    @PostMapping
    public Mono<ResponseEntity<ServiceInstance>> save(@RequestBody ServiceInstanceCreateRequest request) {
        // Creating a service instance both stores it locally and registers it with Eureka.
        return serviceInstancePort.save(request)
                .map(savedInstance -> ResponseEntity.status(HttpStatus.CREATED).body(savedInstance));
    }

    @GetMapping("/{applicationName}")
    public Mono<ResponseEntity<ServiceInstance>> findServiceInstanceByName(@PathVariable String applicationName) {
        // The lookup stays forgiving for callers because ApplicationName normalizes the value internally.
        return serviceInstancePort.findServiceInstanceByName(applicationName)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @GetMapping("/re-register")
    public Mono<ResponseEntity<String>> reRegisterService() {
        return serviceInstancePort.reRegisterService()
                .then(Mono.just(ResponseEntity.ok("Services re-registered successfully")))
                .onErrorResume(error -> Mono.just(
                        ResponseEntity.internalServerError()
                                .body("Failed to re-register services: " + error.getMessage())
                ));
    }

    @DeleteMapping("/de-register/{appName}/{instanceId}")
    public Mono<ResponseEntity<Map<String, Object>>> deRegisterService(
            @PathVariable String appName,
            @PathVariable String instanceId
    ) {
        return serviceInstancePort.deRegisterService(appName, instanceId)
                .map(response -> {
                    boolean success = Boolean.TRUE.equals(response.get("success"));

                    if (success) {
                        return ResponseEntity.ok(response);
                    }

                    return ResponseEntity.badRequest().body(response);
                })
                .onErrorResume(error -> Mono.just(
                        ResponseEntity.internalServerError().body(Map.<String, Object>of(
                                "success", false,
                                "message", "Unexpected error during deregistration",
                                "error", error.getMessage(),
                                "applicationName", appName,
                                "instanceId", instanceId
                        ))
                ));
    }

}
