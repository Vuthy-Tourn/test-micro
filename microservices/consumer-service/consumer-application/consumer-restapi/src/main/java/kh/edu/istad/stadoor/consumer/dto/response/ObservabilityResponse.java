package kh.edu.istad.stadoor.consumer.dto.response;

public record ObservabilityResponse(
        String status,
        String uptime,
        String cpuUsed,
        String diskUsed,
        String memoryUsed
) {
}
