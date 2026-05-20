package kh.edu.istad.stadoor.gateway.service.dto;

public record ServiceOverviewResponse(
        Number totalServices,
        Number activeService,
        Number inactiveService
) {
}
