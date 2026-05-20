package kh.edu.istad.stadoor.eureka.valueobject;

import lombok.Builder;

@Builder
public record ApplicationName(
        String value
) {
}
