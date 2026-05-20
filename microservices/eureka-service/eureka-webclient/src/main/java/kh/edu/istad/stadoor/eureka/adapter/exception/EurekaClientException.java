package kh.edu.istad.stadoor.eureka.adapter.exception;

// Raised when Eureka rejects a request because the request itself is invalid or conflicts with current state.
public class EurekaClientException extends RuntimeException {

    public EurekaClientException(String message) {
        super(message);
    }
}
