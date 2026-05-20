package kh.edu.istad.stadoor.gateway.exception;

import kh.edu.istad.stadoor.gateway.gateway.exception.GatewayAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GatewayAlreadyExistsException.class)
    public Mono<ResponseEntity<ApiError>> handleConflict(
            GatewayAlreadyExistsException ex,
            ServerHttpRequest request
    ) {
        ApiError error = new ApiError(
                409,
                ex.getMessage(),
                "CONFLICT",
                LocalDateTime.now(),
                request.getPath().toString()
        );

        return Mono.just(ResponseEntity.status(409).body(error));
    }

    @ExceptionHandler(GatewayNotFoundException.class)
    public Mono<ResponseEntity<ApiError>> handleGatewayNotFound(
            GatewayNotFoundException ex,
            ServerHttpRequest request
    ) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request.getPath().value());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ApiError>> handleIllegalArgument(
            IllegalArgumentException ex,
            ServerHttpRequest request
    ) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getPath().value());
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ApiError>> handleValidationError(
            WebExchangeBindException ex,
            ServerHttpRequest request
    ) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toValidationMessage)
                .collect(Collectors.joining(", "));

        if (message.isBlank()) {
            message = "Validation failed";
        }

        return buildError(HttpStatus.BAD_REQUEST, message, request.getPath().value());
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ApiError>> handleGenericException(
            Exception ex,
            ServerHttpRequest request
    ) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getPath().value());
    }

    private Mono<ResponseEntity<ApiError>> buildError(HttpStatus status, String message, String path) {
        ApiError apiError = new ApiError(
                status.value(),
                (message == null || message.isBlank()) ? "Unexpected error" : message,
                status.getReasonPhrase(),
                LocalDateTime.now(),
                path
        );

        return Mono.just(ResponseEntity.status(status).body(apiError));
    }

    private String toValidationMessage(FieldError fieldError) {
        String defaultMessage = fieldError.getDefaultMessage();
        if (defaultMessage == null || defaultMessage.isBlank()) {
            defaultMessage = "invalid value";
        }
        return fieldError.getField() + ": " + defaultMessage;
    }
}
