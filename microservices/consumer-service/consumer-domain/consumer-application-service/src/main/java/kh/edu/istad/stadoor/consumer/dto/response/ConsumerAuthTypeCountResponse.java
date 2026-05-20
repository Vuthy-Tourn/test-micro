package kh.edu.istad.stadoor.consumer.dto.response;

import java.util.Map;

public record ConsumerAuthTypeCountResponse(
        long totalConsumers,
        Map<String, Long> byAuthType
) {
}
