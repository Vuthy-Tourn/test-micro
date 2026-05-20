package kh.edu.istad.stadoor.adminbff.dto;

public record ObservabilityResponse(
        String status,
        String uptime,
        String cpuUsed,
        String diskUsed,
        String memoryUsed
) {
}
