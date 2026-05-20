package kh.edu.istad.stadoor.consumer.dto.request;

import java.util.UUID;

public record LoginInput(
        String username,
        String password,
        UUID gatewayId
) {
}
