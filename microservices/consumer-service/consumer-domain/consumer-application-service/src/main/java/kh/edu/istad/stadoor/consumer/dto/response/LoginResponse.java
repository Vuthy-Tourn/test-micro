package kh.edu.istad.stadoor.consumer.dto.response;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        Integer expiresIn
) {
}
