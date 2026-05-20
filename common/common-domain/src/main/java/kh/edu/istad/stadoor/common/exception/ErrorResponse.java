package kh.edu.istad.stadoor.common.exception;

import lombok.Builder;
import java.time.Instant;
import java.util.Map;

@Builder
public record ErrorResponse(
        int status,
        String error,
        String message,
        String path,
        Instant timestamp,
        Map<String, String> details
) {}
