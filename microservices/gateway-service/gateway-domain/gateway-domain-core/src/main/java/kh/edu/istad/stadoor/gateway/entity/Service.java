package kh.edu.istad.stadoor.gateway.entity;

import kh.edu.istad.stadoor.common.valueobject.gateway.GatewayId;
import kh.edu.istad.stadoor.gateway.valueobject.service.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@NoArgsConstructor
public class Service {

    private ServiceId serviceId;
    private GatewayId gatewayId;
    private ServiceName serviceName;
    private ServiceType serviceType;
    private ServiceStatus status;
    private BaseUrl baseUrl;
    private Instant createdAt;
    private Instant updatedAt;

}
