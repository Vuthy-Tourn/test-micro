package kh.edu.istad.stdoor.eureka.entity;

public record NetworkAddress(
        String hostName,
        String ipAddress,
        int port,
        int securePost,
        boolean  secure
) {
}
