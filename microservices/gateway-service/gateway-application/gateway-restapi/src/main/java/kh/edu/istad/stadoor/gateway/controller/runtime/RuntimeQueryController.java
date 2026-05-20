package kh.edu.istad.stadoor.gateway.controller.runtime;

import kh.edu.istad.stadoor.gateway.runtime.dto.GatewayRuntimeResponse;
import kh.edu.istad.stadoor.gateway.runtime.port.input.RuntimeQueryInputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/runtime")
@RequiredArgsConstructor
public class RuntimeQueryController {

    private final RuntimeQueryInputPort runtimeQueryInputPort;

    @GetMapping("/gateways/{gatewayId}")
    public Mono<ResponseEntity<GatewayRuntimeResponse>> getGatewayRuntime(@PathVariable UUID gatewayId) {
        return runtimeQueryInputPort.getGatewayRuntime(gatewayId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/tenants/{tenantId}/gateways/{gatewayName}")
    public Mono<ResponseEntity<GatewayRuntimeResponse>> getGatewayRuntimeByTenantAndName(
            @PathVariable UUID tenantId,
            @PathVariable String gatewayName
    ) {
        return runtimeQueryInputPort.getGatewayRuntimeByTenantAndName(tenantId, gatewayName)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
