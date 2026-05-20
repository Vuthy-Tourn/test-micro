package kh.edu.istad.stadoor.consumer.dto.response;

public record RefreshResponse(
        String accessToken,
        String refreshToken,
        Integer expiresIn
) {
}
