package kh.edu.istad.stadoor.gateway.exception;

import kh.edu.istad.stadoor.gateway.web.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.concurrent.TimeoutException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(RouteNotFoundException.class)
    ResponseEntity<ErrorResponse> handleRouteNotFound(RouteNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "ROUTE_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(InactiveGatewayException.class)
    ResponseEntity<ErrorResponse> handleInactiveGateway(InactiveGatewayException ex) {
        return build(HttpStatus.FORBIDDEN, "INACTIVE_GATEWAY", ex.getMessage());
    }

    @ExceptionHandler(InactiveServiceException.class)
    ResponseEntity<ErrorResponse> handleInactiveService(InactiveServiceException ex) {
        return build(HttpStatus.FORBIDDEN, "INACTIVE_SERVICE", ex.getMessage());
    }

    @ExceptionHandler(InactiveRouteException.class)
    ResponseEntity<ErrorResponse> handleInactiveRoute(InactiveRouteException ex) {
        return build(HttpStatus.FORBIDDEN, "INACTIVE_ROUTE", ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedRequestException.class)
    ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedRequestException ex) {
        return build(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage());
    }

    @ExceptionHandler(TimeoutException.class)
    ResponseEntity<ErrorResponse> handleTimeout(TimeoutException ex) {
        return build(HttpStatus.GATEWAY_TIMEOUT, "UPSTREAM_TIMEOUT", "Upstream service did not respond in time");
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        log.error("Unhandled exception: type={}, message={}", ex.getClass().getName(), ex.getMessage(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", ex.getMessage());
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(code, message, Instant.now()));
    }
}
