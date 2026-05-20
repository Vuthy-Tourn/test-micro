package kh.edu.istad.stadoor.frontbff.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class FallbackControllerTest {

    private final FallbackController fallbackController = new FallbackController();

    @Test
    void gatewayStaticFallbackReturnsServiceUnavailablePayload() {
        var response = fallbackController.gatewayStaticFallback().block();

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody())
                .containsEntry("status", HttpStatus.SERVICE_UNAVAILABLE.value())
                .containsEntry("path", "/gateway-static/**");
    }

    @Test
    void publicNextFallbackDescribesTheNextRuntimePath() {
        var response = fallbackController.publicNextFallback().block();

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody())
                .containsEntry("status", HttpStatus.SERVICE_UNAVAILABLE.value())
                .containsEntry("path", "/_next/**");
    }
}
