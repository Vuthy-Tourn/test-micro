package kh.edu.istad.stadoor.gateway.entity;


import kh.edu.istad.stadoor.common.valueobject.TenantId;
import kh.edu.istad.stadoor.common.valueobject.gateway.GatewayId;
import kh.edu.istad.stadoor.gateway.valueobject.gateway.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Setter
@Getter
@NoArgsConstructor
public class Gateway {

    private GatewayId gatewayId;
    private TenantId tenantId;
    private GatewayName name;
    private GatewayDescription description;
    private kh.edu.istad.stadoor.common.valueobject.gateway.GatewayStatus status;
    private AuthType authType;
    private GatewayType gatewayType;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;


}
