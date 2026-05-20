package kh.edu.istad.stadoor.gateway.controller.gateway;

import kh.edu.istad.stadoor.gateway.gateway.dto.query.DashboardOverviewResponse;
import kh.edu.istad.stadoor.gateway.gateway.dto.query.GatewayDetailResponse;
import kh.edu.istad.stadoor.gateway.gateway.dto.query.GatewaySummaryResponse;
import kh.edu.istad.stadoor.gateway.gateway.dto.query.UserProfilePageResponse;
import kh.edu.istad.stadoor.gateway.gateway.port.input.GatewayQueryInputPort;
import kh.edu.istad.stadoor.gateway.gateway.ports.output.UserProfileOutputPort;
import kh.edu.istad.stadoor.gateway.security.TenantIdJwtExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/gateways")
@RequiredArgsConstructor
public class GatewayQueryController {

    private final GatewayQueryInputPort gatewayQueryInputPort;
    private final TenantIdJwtExtractor tenantIdJwtExtractor;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<GatewaySummaryResponse> getAllGateways() {
        return gatewayQueryInputPort.getAllGateways();
    }

    @GetMapping("/{gatewayId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<GatewayDetailResponse>> getGatewayById(@PathVariable UUID gatewayId) {
        return gatewayQueryInputPort.getGatewayById(gatewayId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/tenant/{tenantId}")
    @ResponseStatus(HttpStatus.OK)
    public Flux<GatewaySummaryResponse> getAllGatewayByTenantId(@PathVariable UUID tenantId) {
        return gatewayQueryInputPort.getAllGatewayByTenantId(tenantId);
    }

    @GetMapping("/my")
    @ResponseStatus(HttpStatus.OK)
    public Flux<GatewaySummaryResponse> getMyGateways(@AuthenticationPrincipal Jwt jwt) {
        var tenantId = tenantIdJwtExtractor.extract(jwt);
        if (tenantId == null) {
            return Flux.empty();
        }
        return gatewayQueryInputPort.getAllGatewayByTenantId(tenantId.id());
    }


    @GetMapping("/dashboard/overview")
    @ResponseStatus(HttpStatus.OK)
    public Mono<DashboardOverviewResponse>getDashboardOverviewByTenantId(@AuthenticationPrincipal Jwt jwt) {
        var tenantId = tenantIdJwtExtractor.extract(jwt);
        if (tenantId == null) {
            return Mono.just(DashboardOverviewResponse.builder()
                    .totalGateways(0L)
                    .totalRoutes(0L)
                    .totalServices(0L)
                    .build());
        }
        return gatewayQueryInputPort.getOverviewByTenantId(tenantId);
    }

}
