package kh.edu.istad.stadoor.gateway.controller.admin;

import kh.edu.istad.stadoor.gateway.admin.dto.query.GlobalOverviewResponse;
import kh.edu.istad.stadoor.gateway.admin.port.input.GlobalOverviewQueryInputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class GlobalOverviewQueryController {

    private final GlobalOverviewQueryInputPort globalOverviewQueryInputPort;

    @GetMapping("/overview")
    @PreAuthorize("permitAll()")
    @ResponseStatus(HttpStatus.OK)
    public Mono<GlobalOverviewResponse> getGlobalOverview() {
        return globalOverviewQueryInputPort.getGlobalOverview();
    }
}
