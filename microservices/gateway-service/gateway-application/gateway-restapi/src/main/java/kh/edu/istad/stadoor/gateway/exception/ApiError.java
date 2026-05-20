package kh.edu.istad.stadoor.gateway.exception;


public record ApiError(
        int status,
        String message,
        String error,
        java.time.LocalDateTime timestamp,
        String path
) {
}
