package kh.edu.istad.stadoor.gateway.gateway.exception;

public abstract class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}