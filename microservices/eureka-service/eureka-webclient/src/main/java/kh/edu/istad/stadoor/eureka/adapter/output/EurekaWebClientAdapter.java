package kh.edu.istad.stadoor.eureka.adapter.output;

import kh.edu.istad.stadoor.eureka.adapter.exception.EurekaClientException;
import kh.edu.istad.stadoor.eureka.adapter.exception.EurekaServerException;
import kh.edu.istad.stadoor.eureka.adapter.exception.ServiceInstanceNotFoundException;
import kh.edu.istad.stadoor.eureka.entity.ServiceInstance;
import kh.edu.istad.stadoor.eureka.port.output.EurekaRegistrationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
@Slf4j
public class EurekaWebClientAdapter implements EurekaRegistrationPort {

    private final WebClient webClient;

    @Override
    public Mono<Void> register(ServiceInstance serviceInstance) {
       return webClient.post()
                .uri("/apps/{appName}", serviceInstance.getApplicationName().value())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("instance", eurekaInstancePayload(serviceInstance)))
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(unused -> System.out.println("Registered service instance with uuid=" + serviceInstance.getUuid()))
                .doOnError(error -> System.err.println("Failed to register service instance with uuid=" + serviceInstance.getUuid() + ": " + error.getMessage()));
    }

    @Override
    public Mono<Void> renewLease(ServiceInstance serviceInstance) {


        log.info("application name:",serviceInstance.getApplicationName().value());
        log.info("serviceId: ",serviceInstance.getUuid());

        return webClient.put()
                .uri("/apps/{appName}/{serviceId}", serviceInstance.getApplicationName().value(), serviceInstance.getUuid())
                .retrieve()
                .toBodilessEntity()
                .then();
    }

    @Override
    public Mono<Void> deRegister(String applicationName, String instanceId) {
        return webClient
                .delete()
                .uri("/apps/{appName}/{instanceId}", applicationName, instanceId)
                .retrieve()
                .onStatus(
                        status -> status.value() == 404,
                        response -> Mono.error(new ServiceInstanceNotFoundException(
                                "Eureka instance not found: applicationName=%s, instanceId=%s"
                                        .formatted(applicationName, instanceId)
                        ))
                )
                .onStatus(
                        status -> status.is4xxClientError(),
                        response -> response.bodyToMono(String.class)
                                .defaultIfEmpty("No response body")
                                .flatMap(body -> Mono.error(new EurekaClientException(
                                        "Eureka returned client error while deregistering instance: "
                                                + "applicationName=" + applicationName
                                                + ", instanceId=" + instanceId
                                                + ", body=" + body
                                )))
                )
                .onStatus(
                        status -> status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .defaultIfEmpty("No response body")
                                .flatMap(body -> Mono.error(new EurekaServerException(
                                        "Eureka server error while deregistering instance: "
                                                + "applicationName=" + applicationName
                                                + ", instanceId=" + instanceId
                                                + ", body=" + body
                                )))
                )
                .toBodilessEntity()
                .doOnSuccess(response ->
                        log.info("Eureka deregistration success: app={}, instanceId={}, status={}",
                                applicationName, instanceId, response.getStatusCode())
                )
                .then();
    }

    private Map<String, Object> eurekaInstancePayload(ServiceInstance request) {

        log.info("instanceId:{} ",request.getUuid());
        log.info("hostName:{} ",request.getNetworkAddress().hostName());
        log.info("app:{} ",request.getApplicationName().value());
        log.info("ipAddr:{} ",request.getNetworkAddress().ipAddress());
        log.info("vipAddress:{} ",request.getApplicationName().value());
        log.info("secureVipAddress:{} ",request.getApplicationName().value());
        log.info("port:{} ",request.getNetworkAddress().port());
        log.info("securePort:{} ",request.getNetworkAddress().securePost());
        log.info("isportenabled:{} ",request.getNetworkAddress().secure());



        Map<String, Object> instance = new LinkedHashMap<>();
        instance.put("instanceId", request.getUuid());
        instance.put("hostName", request.getNetworkAddress().hostName());
        instance.put("app", request.getApplicationName().value());
        instance.put("ipAddr", request.getNetworkAddress().ipAddress());
        instance.put("vipAddress", request.getApplicationName().value());
        instance.put("secureVipAddress", request.getApplicationName().value());
        instance.put("status", "UP");
        instance.put("port", Map.of("$",80, "@enabled", "false"));
        instance.put("securePort", Map.of("$", request.getNetworkAddress().securePost(), "@enabled", String.valueOf(request.getNetworkAddress().secure())));
        instance.put("homePageUrl", request.getHealthCheckEndpoint().homePageUrl());
        instance.put("statusPageUrl", request.getHealthCheckEndpoint().statusUrl());
        instance.put("healthCheckUrl", request.getHealthCheckEndpoint().healthUrl());
        instance.put("leaseInfo",Map.of(
                "renewalIntervalInSecs",request.getLeasePolicy().renewalIntervalSeconds(),
                "durationInSecs",request.getLeasePolicy().expirationDurationSeconds()
        ));
        instance.put("dataCenterInfo", Map.of(
                "@class", "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo",
                "name", "MyOwn"
        ));
        instance.put("metadata", request.getMetadata() == null ? Map.of() : request.getMetadata().metadata());
        return instance;
    }
}
