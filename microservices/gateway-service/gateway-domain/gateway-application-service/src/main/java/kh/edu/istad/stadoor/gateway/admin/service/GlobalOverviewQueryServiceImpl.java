package kh.edu.istad.stadoor.gateway.admin.service;

import kh.edu.istad.stadoor.gateway.admin.dto.query.GlobalOverviewResponse;
import kh.edu.istad.stadoor.gateway.admin.port.input.GlobalOverviewQueryInputPort;
import kh.edu.istad.stadoor.gateway.admin.port.output.GlobalOverviewQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;

//@Service
//@RequiredArgsConstructor
//public class GlobalOverviewQueryServiceImpl implements GlobalOverviewQueryInputPort {
//
//    private final GlobalOverviewQueryOutputPort globalOverviewQueryOutputPort;
//
//    @Override
//    public Mono<GlobalOverviewResponse> getGlobalOverview() {
//        return Mono.zip(
//                globalOverviewQueryOutputPort.countAllGateways(),
//                globalOverviewQueryOutputPort.countAllServices(),
//                globalOverviewQueryOutputPort.countAllRoutes()
//        ).map(tuple -> new GlobalOverviewResponse(
//                tuple.getT1(),
//                tuple.getT2(),
//                tuple.getT3()
//        ));
//    }
//}

//package kh.edu.istad.stadoor.gateway.admin.service;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class GlobalOverviewQueryServiceImpl implements GlobalOverviewQueryInputPort {

    private final GlobalOverviewQueryOutputPort globalOverviewQueryOutputPort;

    @Override
    public Mono<GlobalOverviewResponse> getGlobalOverview() {

        Instant startOfLastMonth = YearMonth.now().minusMonths(1)
                .atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endOfLastMonth = YearMonth.now()
                .atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant startOfThisMonth = endOfLastMonth;
        Instant now = Instant.now();

        Mono<Long> totalGateways  = globalOverviewQueryOutputPort.countAllGateways();
        Mono<Long> totalServices  = globalOverviewQueryOutputPort.countAllServices();
        Mono<Long> totalRoutes    = globalOverviewQueryOutputPort.countAllRoutes();

        Mono<Long> lastGateways   = globalOverviewQueryOutputPort.countGatewaysBetween(startOfLastMonth, endOfLastMonth);
        Mono<Long> lastServices   = globalOverviewQueryOutputPort.countServicesBetween(startOfLastMonth, endOfLastMonth);
        Mono<Long> lastRoutes     = globalOverviewQueryOutputPort.countRoutesBetween(startOfLastMonth, endOfLastMonth);

        Mono<Long> thisGateways   = globalOverviewQueryOutputPort.countGatewaysBetween(startOfThisMonth, now);
        Mono<Long> thisServices   = globalOverviewQueryOutputPort.countServicesBetween(startOfThisMonth, now);
        Mono<Long> thisRoutes     = globalOverviewQueryOutputPort.countRoutesBetween(startOfThisMonth, now);

        return Mono.zip(
                Mono.zip(totalGateways, totalServices, totalRoutes),
                Mono.zip(lastGateways,  lastServices,  lastRoutes),
                Mono.zip(thisGateways,  thisServices,  thisRoutes)
        ).map(outer -> {
            var totals    = outer.getT1(); // Tuple3<Long, Long, Long>
            var lastMonth = outer.getT2(); // Tuple3<Long, Long, Long>
            var thisMonth = outer.getT3(); // Tuple3<Long, Long, Long>

            return new GlobalOverviewResponse(
                    totals.getT1(),
                    totals.getT2(),
                    totals.getT3(),
                    calcChange(thisMonth.getT1(), lastMonth.getT1()),
                    calcChange(thisMonth.getT2(), lastMonth.getT2()),
                    calcChange(thisMonth.getT3(), lastMonth.getT3())
            );
        });
    }

    private double calcChange(long current, long previous) {
        if (previous == 0) return current > 0 ? 100.0 : 0.0;
        return Math.round(((double)(current - previous) / previous) * 10000.0) / 100.0;
    }
}