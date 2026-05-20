package kh.edu.istad.stadoor.common.exception;

import lombok.Builder;
import java.time.Instant;

@Builder
public record SuccessResponse<T>(
        int status,
        String message,
        T data,
        Instant timestamp
) {
}
