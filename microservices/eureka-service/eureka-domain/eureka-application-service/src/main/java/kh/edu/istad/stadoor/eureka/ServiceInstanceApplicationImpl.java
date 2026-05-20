package kh.edu.istad.stadoor.eureka;

import kh.edu.istad.stdoor.eureka.dto.ServiceInstanceCreateRequest;
import kh.edu.istad.stadoor.eureka.entity.ServiceInstance;
import kh.edu.istad.stadoor.eureka.mapper.ServiceInstanceDomainMapper;
import kh.edu.istad.stadoor.eureka.port.output.EurekaRegistrationPort;
import kh.edu.istad.stadoor.eureka.port.input.ServiceInstancePort;
import kh.edu.istad.stadoor.eureka.port.output.ServiceInstanceRepositoryPort;
import kh.edu.istad.stadoor.eureka.valueobject.ApplicationName;
import kh.edu.istad.stadoor.eureka.valueobject.InstanceStatus;

import kh.edu.istad.stdoor.eureka.event.ServiceInstanceCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class ServiceInstanceApplicationImpl implements ServiceInstancePort {

    private static final Duration LEASE_RENEW_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration LEASE_RENEW_BACKOFF = Duration.ofSeconds(2);
    private static final int LEASE_RENEW_MAX_RETRIES = 3;
    private static final int MARK_STALE_AFTER_FAILURES = 3;
    private static final int REMOVE_AFTER_FAILURES = 5;

    private final ServiceInstanceRepositoryPort serviceInstanceRepositoryPort;
    private final EurekaRegistrationPort eurekaRegistrationPort;
    private final ServiceInstanceDomainMapper serviceInstanceMapper;
    private final Map<String, Integer> consecutiveRenewalFailures = new ConcurrentHashMap<>();


    @Override
    public Mono<ServiceInstance> save(ServiceInstanceCreateRequest instanceCreateRequest) {
        log.info("Received registration request: {}", instanceCreateRequest);
        return Mono.just(instanceCreateRequest)
                .map(serviceInstanceMapper::toServiceInstanceDomain)
                .doOnNext(ServiceInstance::validateForRegistration)
                .flatMap(serviceInstance ->eurekaRegistrationPort.register(serviceInstance)
                        .then(serviceInstanceRepositoryPort.save(serviceInstance)))
                .doOnSuccess(serviceInstance -> log.info(
                        "Registered and saved service instance uuid={}",
                        serviceInstance.getUuid()
                ));
    }



    @Override
    public Mono<Void> reRegisterService() {
        return serviceInstanceRepositoryPort.findAllInstances()
                .flatMap(instance ->
                        eurekaRegistrationPort.register(instance)
                                .doOnSuccess(unused ->
                                        log.info("Re-registered service: {}", instance.getUuid())
                                )
                                .doOnError(error ->
                                        log.error("Failed to re-register service: {} - {}",
                                                instance.getUuid(),
                                                error.getMessage())
                                )
                                .onErrorResume(error -> Mono.empty())
                )
                .then();
    }



    @Override
    public Mono<ServiceInstance> findServiceInstanceByName(String applicationName) {
        return serviceInstanceRepositoryPort.findAllByApplicationName(new ApplicationName(applicationName))
                .flatMap(serviceInstances -> serviceInstances.isEmpty()
                        ? Mono.empty()
                        : Mono.just(serviceInstances.get(0)))
                .doOnSuccess(serviceInstance -> {
                    if (serviceInstance != null) {
                        log.info("Found service instance applicationName={} uuid={}",
                                applicationName,
                                serviceInstance.getUuid());
                    }
                });
    }

    @Override
    public Mono<Void> Consume(ServiceInstanceCreatedEvent event) {

           return null;
    }

    @Override
    public Mono<Map<String, Object>> deRegisterService(String applicationName, String instanceId) {

        return eurekaRegistrationPort.deRegister(applicationName, instanceId)
                .then(serviceInstanceRepositoryPort.deleteById(instanceId))
                .doOnSuccess(unused ->
                        log.info("Successfully deregistered service and removed from DB: applicationName={}, instanceId={}",
                                applicationName, instanceId)
                )
                .thenReturn(Map.<String, Object>of(
                        "success", true,
                        "message", "Service deregistered successfully and removed from DB",
                        "applicationName", applicationName,
                        "instanceId", instanceId
                ))
                .onErrorResume(error -> {
                    log.error("Failed to deregister service or remove from DB: applicationName={}, instanceId={}, error={}",
                            applicationName, instanceId, error.getMessage(), error);

                    return Mono.just(Map.<String, Object>of(
                            "success", false,
                            "message", "Failed to deregister service or remove from DB",
                            "applicationName", applicationName,
                            "instanceId", instanceId,
                            "error", error.getMessage()
                    ));
                });
    }


    @Scheduled(fixedDelayString = "30000")
    public void renewRegisteredServicesBatch() {
        log.info("heartbeat renewal ....!");
        serviceInstanceRepositoryPort.findAllInstances()
                .flatMap(this::renewLeaseWithResilience)
                .then()
                .subscribe();
    }

    private Mono<Void> renewLeaseWithResilience(ServiceInstance serviceInstance) {
        return Mono.defer(() -> eurekaRegistrationPort.renewLease(serviceInstance))
                .timeout(LEASE_RENEW_TIMEOUT)
                .retryWhen(Retry.backoff(LEASE_RENEW_MAX_RETRIES, LEASE_RENEW_BACKOFF)
                        .jitter(0.5d)
                        .doBeforeRetry(retrySignal -> log.warn(
                                "Retrying lease renewal for uuid={} attempt={} reason={}",
                                serviceInstance.getUuid(),
                                retrySignal.totalRetries() + 1,
                                retrySignal.failure().getMessage()
                        )))
                .doOnSuccess(unused -> clearRenewalFailures(serviceInstance))
                .onErrorResume(error -> handleRenewalFailure(serviceInstance, error));
    }

    private void clearRenewalFailures(ServiceInstance serviceInstance) {
        Integer previousFailures = consecutiveRenewalFailures.remove(serviceInstance.getUuid());
        if (previousFailures != null && previousFailures > 0) {
            log.info("Lease renewal recovered for uuid={} after {} failed cycles",
                    serviceInstance.getUuid(), previousFailures);
        } else {
            log.info("Renewed service instance uuid={}", serviceInstance.getUuid());
        }
    }

    private Mono<Void> handleRenewalFailure(ServiceInstance serviceInstance, Throwable error) {
        int failureCount = consecutiveRenewalFailures.merge(serviceInstance.getUuid(), 1, Integer::sum);
        log.error("Failed to renew service instance uuid={} failureCount={} reason={}",
                serviceInstance.getUuid(), failureCount, error.getMessage(), error);

        if (failureCount >= REMOVE_AFTER_FAILURES) {
            return serviceInstanceRepositoryPort.deleteById(serviceInstance.getUuid())
                    .doOnSuccess(unused -> {
                        consecutiveRenewalFailures.remove(serviceInstance.getUuid());
                        log.warn("Removed service instance uuid={} after {} failed renewal cycles",
                                serviceInstance.getUuid(), failureCount);
                    })
                    .onErrorResume(deleteError -> {
                        log.error("Failed to remove stale service instance uuid={} reason={}",
                                serviceInstance.getUuid(), deleteError.getMessage(), deleteError);
                        return Mono.empty();
                    });
        }

        if (failureCount >= MARK_STALE_AFTER_FAILURES && serviceInstance.getStatus() != InstanceStatus.OUT_OF_SERVICE) {
            ServiceInstance staleInstance = serviceInstance.withStatus(InstanceStatus.OUT_OF_SERVICE);
            return serviceInstanceRepositoryPort.save(staleInstance)
                    .doOnSuccess(savedInstance -> log.warn(
                            "Marked service instance uuid={} as {} after {} failed renewal cycles",
                            savedInstance.getUuid(),
                            savedInstance.getStatus(),
                            failureCount
                    ))
                    .then()
                    .onErrorResume(saveError -> {
                        log.error("Failed to mark service instance uuid={} as stale reason={}",
                                serviceInstance.getUuid(), saveError.getMessage(), saveError);
                        return Mono.empty();
                    });
        }

        return Mono.empty();
    }
}
