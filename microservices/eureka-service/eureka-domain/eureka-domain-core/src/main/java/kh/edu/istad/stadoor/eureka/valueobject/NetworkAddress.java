package kh.edu.istad.stadoor.eureka.valueobject;

public record NetworkAddress(
        String hostName,
        String ipAddress,
        int port,
        int securePost,
        boolean  secure
) {
}
