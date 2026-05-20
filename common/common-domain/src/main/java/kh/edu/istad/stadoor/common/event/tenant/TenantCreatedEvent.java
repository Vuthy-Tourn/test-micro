package kh.edu.istad.stadoor.common.event.tenant;


import kh.edu.istad.stadoor.common.valueobject.TenantId;
import kh.edu.istad.stadoor.common.valueobject.UserId;
import lombok.Builder;

@Builder
public record TenantCreatedEvent(
        TenantId tenantId,
        UserId ownerUserId,
        String name,
        Integer maxGateway,
        Integer maxClient
) {
}
