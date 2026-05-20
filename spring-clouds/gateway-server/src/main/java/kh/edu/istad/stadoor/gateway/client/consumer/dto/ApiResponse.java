package kh.edu.istad.stadoor.gateway.client.consumer.dto;

public record ApiResponse<T>(
        String message,
        T data
) {
}
