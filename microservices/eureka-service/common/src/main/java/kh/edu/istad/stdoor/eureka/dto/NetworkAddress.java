package kh.edu.istad.stdoor.eureka.dto;

import lombok.Builder;
import lombok.ToString;

@Builder
public record NetworkAddress(

        String hostName,
        String ipAddress,
        int port,
        int securePost,
        boolean  secure

) {
}
