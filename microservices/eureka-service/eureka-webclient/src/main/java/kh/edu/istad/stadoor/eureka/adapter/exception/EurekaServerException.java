package kh.edu.istad.stadoor.eureka.adapter.exception;

// Raised when Eureka itself fails while processing an otherwise valid request.
public class EurekaServerException extends RuntimeException {

    public EurekaServerException(String message) {
        super(message);
    }
}
