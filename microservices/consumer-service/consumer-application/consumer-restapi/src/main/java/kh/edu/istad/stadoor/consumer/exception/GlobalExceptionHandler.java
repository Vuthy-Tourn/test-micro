package kh.edu.istad.stadoor.consumer.exception;

import kh.edu.istad.stadoor.consumer.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<ApiResponse<Void>> handleNotFound(DomainNotFoundException ex) {
        log.warn("Not found: {}", ex.getMessage());
        return Mono.just(ApiResponse.error("Resource not found"));
    }

    @ExceptionHandler(DomainUnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Mono<ApiResponse<Void>> handleUnauthorized(DomainUnauthorizedException ex) {
        log.warn("Unauthorized: {}", ex.getMessage());
        return Mono.just(ApiResponse.error("Unauthorized"));
    }

    @ExceptionHandler(DomainConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Mono<ApiResponse<Void>> handleConflict(DomainConflictException ex) {
        log.warn("Conflict: {}", ex.getMessage());
        return Mono.just(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(DomainForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Mono<ApiResponse<Void>> handleForbidden(DomainForbiddenException ex) {
        log.warn("Forbidden: {}", ex.getMessage());
        return Mono.just(ApiResponse.error("Access denied"));
    }

    @ExceptionHandler(ServerWebInputException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ApiResponse<Void>> handleServerWebInput(ServerWebInputException ex) {
        log.warn("Invalid request input: {}", ex.getMessage());
        return Mono.just(ApiResponse.error("Invalid request body"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return Mono.just(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ApiResponse<Void>> handleValidation(WebExchangeBindException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Validation failed");
        return Mono.just(ApiResponse.error(message));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleResponseStatus(ResponseStatusException ex) {
        log.warn("Response status exception: {}", ex.getMessage());
        return Mono.just(ResponseEntity.status(ex.getStatusCode())
                .body(ApiResponse.error(ex.getReason())));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<ApiResponse<Void>> handleGeneric(Exception ex) {
        log.error("Unhandled exception", ex);
        return Mono.just(ApiResponse.error("Internal server error"));
    }
}
