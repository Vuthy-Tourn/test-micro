package kh.edu.istad.stadoor.gateway.controller.admin.dto;

public record ObservabilityResponse(
        String status,
        String uptime,
        String cpuUsed,
        String diskUsed,
        String memoryUsed
) {
}
