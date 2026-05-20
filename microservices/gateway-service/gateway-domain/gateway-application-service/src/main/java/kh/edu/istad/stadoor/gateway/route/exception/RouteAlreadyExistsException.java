package kh.edu.istad.stadoor.gateway.route.exception;

import kh.edu.istad.stadoor.gateway.gateway.exception.BusinessException;

public class RouteAlreadyExistsException extends BusinessException {
    public RouteAlreadyExistsException(String message) {
        super(message);
    }
}
