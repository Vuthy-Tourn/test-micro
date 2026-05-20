package kh.edu.istad.stdoor.eureka.event.valueObject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.UUID;

@Builder
public record ServiceId(
        UUID serviceId
) {
}
