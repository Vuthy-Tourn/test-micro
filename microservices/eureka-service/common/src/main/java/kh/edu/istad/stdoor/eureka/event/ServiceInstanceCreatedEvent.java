package kh.edu.istad.stdoor.eureka.event;

import kh.edu.istad.stdoor.eureka.event.valueObject.BaseUrl;
import kh.edu.istad.stdoor.eureka.event.valueObject.GatewayId;
import kh.edu.istad.stdoor.eureka.event.valueObject.Name;
import kh.edu.istad.stdoor.eureka.event.valueObject.ServiceId;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Builder
@Data
public class ServiceInstanceCreatedEvent
{
    private ServiceId serviceId;
    private GatewayId gatewayId;
    private Name name;
    private String type;
    private BaseUrl baseUrl;
    private String status;
//    private Instant createdAt;
}
