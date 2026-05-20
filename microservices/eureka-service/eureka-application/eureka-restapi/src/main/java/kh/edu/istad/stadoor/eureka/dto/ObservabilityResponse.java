package kh.edu.istad.stadoor.eureka.dto;

public record ObservabilityResponse(
        String status,
        String uptime,
        String cpuUsed,
        String diskUsed,
        String memoryUsed
) {
}
