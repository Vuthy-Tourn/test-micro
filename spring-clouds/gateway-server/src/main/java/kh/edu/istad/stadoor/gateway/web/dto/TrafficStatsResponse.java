package kh.edu.istad.stadoor.gateway.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TrafficStatsResponse(
        Integer year,
        Long    total,
        String  peakMonth,
        Long    peakRequests,
        List<MonthData>    data,
        Map<String, Long>  allMonths
) {
    @Builder
    public record MonthData(
            String month,
            String yearMonth,
            long   requests
    ) {}
}
