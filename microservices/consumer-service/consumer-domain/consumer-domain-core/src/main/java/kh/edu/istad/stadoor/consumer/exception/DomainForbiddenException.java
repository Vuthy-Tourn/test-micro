package kh.edu.istad.stadoor.consumer.exception;

public class DomainForbiddenException extends RuntimeException {
    public DomainForbiddenException(String message) {
        super(message);
    }
}
