package kh.edu.istad.stadoor.gateway.runtime.model;

public record ResolvedRuntimeConfig(
        GatewayRuntimeConfig runtimeConfig,
        String remainingPath
) {
}
