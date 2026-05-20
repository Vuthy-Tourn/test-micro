package kh.edu.istad.stadoor.gateway.runtime.model;

import java.util.List;
import java.util.UUID;

public record ConsumerContext(
        UUID consumerId,
        UUID tenantId,
        UUID gatewayId,
        List<String> roles
) {
    public static final ConsumerContext EMPTY = new ConsumerContext(null, null, null, List.of());
}
