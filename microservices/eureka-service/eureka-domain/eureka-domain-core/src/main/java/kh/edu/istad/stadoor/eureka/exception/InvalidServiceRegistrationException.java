package kh.edu.istad.stadoor.eureka.exception;

public class InvalidServiceRegistrationException extends RuntimeException {

    public InvalidServiceRegistrationException(String message) {
        super(message);
    }

    public InvalidServiceRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
