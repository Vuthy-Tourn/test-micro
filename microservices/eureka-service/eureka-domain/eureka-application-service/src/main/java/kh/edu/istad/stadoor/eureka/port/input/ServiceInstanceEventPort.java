package kh.edu.istad.stadoor.eureka.port.input;

import kh.edu.istad.stdoor.eureka.event.ServiceInstanceCreatedEvent;

public interface ServiceInstanceEventPort {
    void consume(String event);
}
