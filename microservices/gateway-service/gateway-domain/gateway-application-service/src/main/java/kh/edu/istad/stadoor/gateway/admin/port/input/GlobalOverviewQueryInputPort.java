package kh.edu.istad.stadoor.gateway.admin.port.input;

import kh.edu.istad.stadoor.gateway.admin.dto.query.GlobalOverviewResponse;
import reactor.core.publisher.Mono;

public interface GlobalOverviewQueryInputPort {

    Mono<GlobalOverviewResponse> getGlobalOverview();
}
