package kh.edu.istad.stadoor.gateway.gateway.dto.query;

import java.util.List;

public record UserProfilePageResponse(
        List<UserProfileResponse> content,
        long totalElements,
        int totalPages,
        int pageNumber,
        int pageSize
) {}
