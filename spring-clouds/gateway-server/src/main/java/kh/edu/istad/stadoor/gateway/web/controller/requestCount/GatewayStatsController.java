package kh.edu.istad.stadoor.gateway.web.controller.requestCount;


import kh.edu.istad.stadoor.gateway.runtime.requestCount.RequestCounterService;
import kh.edu.istad.stadoor.gateway.web.dto.TrafficStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Map;

@RestController
@RequestMapping("/actuator/gateway")
@RequiredArgsConstructor
public class GatewayStatsController {

    private final RequestCounterService counterService;

    // GET /actuator/gateway/traffic
    // GET /actuator/gateway/traffic?year=2025
    @GetMapping("/traffic")
    public Mono<ResponseEntity<TrafficStatsResponse>> getTrafficChart(
            @RequestParam(defaultValue = "0") int year) {

        int targetYear = year == 0 ? LocalDate.now().getYear() : year;

        return counterService.getYearlyChart(targetYear)
                .map(data -> {
                    long total = data.stream()
                            .mapToLong(TrafficStatsResponse.MonthData::requests)
                            .sum();

                    TrafficStatsResponse.MonthData peak = data.stream()
                            .max(Comparator.comparingLong(TrafficStatsResponse.MonthData::requests))
                            .orElse(null);

                    return ResponseEntity.ok(
                            TrafficStatsResponse.builder()
                                    .year(targetYear)
                                    .total(total)
                                    .peakMonth(peak != null ? peak.month() : null)
                                    .peakRequests(peak != null ? peak.requests() : null)
                                    .data(data)
                                    .build()
                    );
                });
    }

    // GET /actuator/gateway/stats
    // GET /actuator/gateway/stats?month=2025-03
    // GET /actuator/gateway/stats?from=2025-01&to=2025-06
    @GetMapping("/stats")
    public Mono<ResponseEntity<?>> getStats(
            @RequestParam(required = false) String month,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {

        // single month
        if (month != null) {
            return counterService.getByMonth(month)
                    .map(count -> ResponseEntity.ok(
                            Map.of("month", month, "totalRequests", count)
                    ));
        }

        // date range
        if (from != null && to != null) {
            return counterService.getByRange(from, to)
                    .map(months -> ResponseEntity.ok(
                            TrafficStatsResponse.builder()
                                    .allMonths(months)
                                    .build()
                    ));
        }

        // all months
        return counterService.getAllMonths()
                .map(months -> ResponseEntity.ok(
                        TrafficStatsResponse.builder()
                                .allMonths(months)
                                .build()
                ));
    }

    // DELETE /actuator/gateway/stats?month=2025-03
    @DeleteMapping("/stats")
    public Mono<ResponseEntity<?>> resetStats(
            @RequestParam(required = false) String month) {

        if (month == null) {
            return Mono.just(ResponseEntity.badRequest().body(
                    Map.of("error", "?month=yyyy-MM param is required")
            ));
        }

        return counterService.resetMonth(month)
                .map(ok -> ResponseEntity.ok(
                        Map.of("reset", ok, "month", month)
                ));
    }
}
