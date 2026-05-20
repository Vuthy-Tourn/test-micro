package kh.edu.istad.stadoor.eureka.adapter.exception;

// Raised when we try to renew or deregister an instance that Eureka no longer knows about.
public class ServiceInstanceNotFoundException extends RuntimeException {

    public ServiceInstanceNotFoundException(String message) {
        super(message);
    }
}
