package kh.edu.istad.stdoor.eureka.dto;

import lombok.Builder;

import java.util.Map;

@Builder
public record Metadata(
        Map<String, String> metadata
) {
}
