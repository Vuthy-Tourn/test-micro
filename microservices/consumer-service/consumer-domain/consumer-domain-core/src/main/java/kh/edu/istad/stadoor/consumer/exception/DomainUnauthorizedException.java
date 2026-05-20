package kh.edu.istad.stadoor.consumer.exception;

public class DomainUnauthorizedException extends RuntimeException {
    public DomainUnauthorizedException(String message) {
        super(message);
    }
}
