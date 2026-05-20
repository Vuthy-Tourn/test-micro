package kh.edu.istad.stadoor.gateway.service.exception;

import kh.edu.istad.stadoor.gateway.gateway.exception.BusinessException;

public class ServiceAlreadyExistsException extends BusinessException {
    public ServiceAlreadyExistsException(String message) {
        super(message);
    }
}
