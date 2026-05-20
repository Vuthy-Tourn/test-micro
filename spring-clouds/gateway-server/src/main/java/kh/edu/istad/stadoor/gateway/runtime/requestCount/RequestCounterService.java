package kh.edu.istad.stadoor.gateway.runtime.requestCount;

import kh.edu.istad.stadoor.gateway.web.dto.TrafficStatsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestCounterService {

    private final ReactiveStringRedisTemplate redis;

    private static final String PREFIX      = "gateway:requests:";
    private static final DateTimeFormatter FMT        = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final DateTimeFormatter MONTH_LABEL = DateTimeFormatter.ofPattern("MMM");

    // ── increment current month ──────────────────────────────────────────────
    public Mono<Long> increment() {
        String key = PREFIX + LocalDate.now().format(FMT);
        return redis.opsForValue().increment(key);
    }

    // ── single month  e.g. "2025-03" ────────────────────────────────────────
    public Mono<Long> getByMonth(String yearMonth) {
        return redis.opsForValue()
                .get(PREFIX + yearMonth)
                .map(Long::parseLong)
                .defaultIfEmpty(0L);
    }

    // ── full year  e.g. 2025 ────────────────────────────────────────────────
    public Mono<List<TrafficStatsResponse.MonthData>> getYearlyChart(int year) {
        List<String> keys = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            keys.add(PREFIX + String.format("%d-%02d", year, m));
        }

        return Flux.fromIterable(keys)
                .flatMapSequential(key ->
                        redis.opsForValue().get(key)
                                .defaultIfEmpty("0")
                                .map(val -> {
                                    String ym = key.replace(PREFIX, "");
                                    LocalDate d = YearMonth.parse(ym, FMT).atDay(1);
                                    return TrafficStatsResponse.MonthData.builder()
                                            .month(d.format(MONTH_LABEL))
                                            .yearMonth(ym)
                                            .requests(Long.parseLong(val))
                                            .build();
                                })
                )
                .collectList();
    }

    // ── date range  e.g. "2025-01" → "2025-06" ──────────────────────────────
    public Mono<Map<String, Long>> getByRange(String from, String to) {
        return redis.keys(PREFIX + "*")
                .filter(key -> {
                    String ym = key.replace(PREFIX, "");
                    return ym.compareTo(from) >= 0 && ym.compareTo(to) <= 0;
                })
                .flatMap(key -> redis.opsForValue().get(key)
                        .defaultIfEmpty("0")
                        .map(v -> Map.entry(key.replace(PREFIX, ""), Long.parseLong(v)))
                )
                .collectSortedList(Map.Entry.comparingByKey())
                .map(entries -> {
                    Map<String, Long> result = new LinkedHashMap<>();
                    entries.forEach(e -> result.put(e.getKey(), e.getValue()));
                    return result;
                });
    }

    // ── all months ever stored ───────────────────────────────────────────────
    public Mono<Map<String, Long>> getAllMonths() {
        return redis.keys(PREFIX + "*")
                .flatMap(key -> redis.opsForValue().get(key)
                        .defaultIfEmpty("0")
                        .map(v -> Map.entry(key.replace(PREFIX, ""), Long.parseLong(v)))
                )
                .collectSortedList(Map.Entry.comparingByKey())
                .map(entries -> {
                    Map<String, Long> result = new LinkedHashMap<>();
                    entries.forEach(e -> result.put(e.getKey(), e.getValue()));
                    return result;
                });
    }

    // ── reset one month ──────────────────────────────────────────────────────
    public Mono<Boolean> resetMonth(String yearMonth) {
        return redis.delete(PREFIX + yearMonth)
                .map(n -> n > 0);
    }
}
