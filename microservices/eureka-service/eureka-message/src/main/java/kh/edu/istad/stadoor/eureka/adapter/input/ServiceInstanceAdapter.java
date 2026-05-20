package kh.edu.istad.stadoor.eureka.adapter.input;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kh.edu.istad.stadoor.eureka.ServiceInstanceApplicationImpl;
import kh.edu.istad.stadoor.eureka.port.input.ServiceInstanceEventPort;
import kh.edu.istad.stdoor.eureka.dto.*;
import kh.edu.istad.stdoor.eureka.event.ServiceInstanceCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ServiceInstanceAdapter implements ServiceInstanceEventPort {

    private final ObjectMapper objectMapper;
    private final ServiceInstanceApplicationImpl serviceInstanceApplication;


    @Override
    @KafkaListener(
            groupId = "eureka-consumer-group",
            topics = "Stadoor.Service.Event"
    )
    public void consume(String message) {
        try {


            ServiceInstanceCreatedEvent event =
                    objectMapper.readValue(message, ServiceInstanceCreatedEvent.class);

          ServiceInstanceCreateRequest request=  this.toServiceInstanceRequest(event);

          serviceInstanceApplication.save(request).subscribe();


        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize Kafka message: {}", message, e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private ServiceInstanceCreateRequest toServiceInstanceRequest(
            ServiceInstanceCreatedEvent serviceEventCreated
    ) throws UnknownHostException {
        String url=serviceEventCreated.getBaseUrl().baseUrl();
        ServiceInstanceCreateRequest request=ServiceInstanceCreateRequest.builder()
                .applicationName(this.hostNameHandler(url))
                .instanceServiceId(serviceEventCreated.getServiceId().serviceId().toString())
                .networkAddress(
                        NetworkAddress.builder()
                                .hostName(this.hostNameHandler(url))
                                .ipAddress(this.ipHandler(url))
                                .port(80)
                                .secure(true)
                                .securePost(443)
                                .build()
                )
                .leasePolicy(
                        new LeasePolicy(30,90)
                )
                .healthCheckEndpoint(
                        new HealthCheckEndpoint(
                                "https://dummyjson.com/actuator/health",
                                "https://dummyjson.com/actuator/info",
                                "https://dummyjson.com"

                        )
                )
                .instanceStatus(InstanceStatus.UP)
                .metadata(

                       additionalInfo()
                )
                .build();



        return request;
    }
    private Metadata additionalInfo(){
        Map<String, String> map = new HashMap<>();
        map.put("version", "1.0.0");
        map.put("zone", "local");
        map.put("profile", "dev");

     return   Metadata.builder()
                .metadata(map)
                .build();


    }

    private String ipHandler(String url) throws UnknownHostException {
       String hostName=this.hostNameHandler(url);
        InetAddress address=InetAddress.getByName(hostName);
        return address.getHostAddress();

    }

    private String hostNameHandler(String url){
        URI uri=URI.create(url);
         String hostName=uri.getHost();
        return hostName;
    }

    private int portHandler(String url){
        URI uri=URI.create(url);
        int port = uri.getPort();
        int actualPort=port!=-1
                ?port
                :uri.getScheme().equals("https")?443:80;
        log.info("port:{}",actualPort);
        return actualPort;
    }

}
