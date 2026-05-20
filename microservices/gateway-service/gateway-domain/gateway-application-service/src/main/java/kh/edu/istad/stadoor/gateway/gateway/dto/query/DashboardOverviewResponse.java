package kh.edu.istad.stadoor.gateway.gateway.dto.query;

import lombok.Builder;

@Builder
public record DashboardOverviewResponse(
    long totalGateways,
    long totalServices,
    long totalRoutes
) {
}
