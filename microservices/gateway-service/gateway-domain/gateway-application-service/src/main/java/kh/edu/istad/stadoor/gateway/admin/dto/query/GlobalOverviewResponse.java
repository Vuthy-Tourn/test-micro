package kh.edu.istad.stadoor.gateway.admin.dto.query;

public record GlobalOverviewResponse(
        long totalGateways,
        long totalServices,
        long totalRoutes,

        double gatewayChangePercent,
        double serviceChangePercent,
        double routeChangePercent
) {
}
