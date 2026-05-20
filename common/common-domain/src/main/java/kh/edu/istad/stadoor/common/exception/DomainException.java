package kh.edu.istad.stadoor.common.exception;

import lombok.Getter;

@Getter
public class DomainException extends RuntimeException {
    private final int status;

    public DomainException(String message) {
        this(message, 400);
    }

    public DomainException(String message, int status) {
        super(message);
        this.status = status;
    }

    public DomainException(String message, Throwable throwable) {
        this(message, 400, throwable);
    }

    public DomainException(String message, int status, Throwable throwable) {
        super(message, throwable);
        this.status = status;
    }

}
