package kh.edu.istad.stadoor.gateway.validate;

import kh.edu.istad.stadoor.common.valueobject.gateway.GatewayId;
import kh.edu.istad.stadoor.gateway.valueobject.service.*;

public class ServiceValidate {

    public static void validateRegister(
            ServiceId serviceId,
            GatewayId gatewayId,
            ServiceName name,
            ServiceType type,
            BaseUrl baseUrl
    ) {
        if (serviceId == null) {
            throw new IllegalArgumentException("ServiceId must not be null");
        }
        if (gatewayId == null) {
            throw new IllegalArgumentException("GatewayId must not be null");
        }
        if (name == null || name.name().isBlank()) {
            throw new IllegalArgumentException("Service name must not be empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Service type must not be null");
        }
        if (baseUrl == null || baseUrl.baseUrl().isBlank()) {
            throw new IllegalArgumentException("Base URL must not be empty");
        }
    }

    public static void validateUpdate(
            ServiceStatus currentStatus,
            ServiceName name,
            BaseUrl baseUrl
    ) {
        if (name == null || name.name().isBlank()) {
            throw new IllegalArgumentException("Service name must not be empty");
        }
        if (baseUrl == null || baseUrl.baseUrl().isBlank()) {
            throw new IllegalArgumentException("Base URL must not be empty");
        }
    }

    public static void validateActivate(ServiceStatus currentStatus) {
        if (currentStatus == ServiceStatus.ACTIVE) {
            throw new IllegalStateException("Service is already active");
        }
    }

    public static void validateDeactivate(ServiceStatus currentStatus) {
        if (currentStatus == ServiceStatus.INACTIVE) {
            throw new IllegalStateException("Service is already inactive");
        }
    }
}