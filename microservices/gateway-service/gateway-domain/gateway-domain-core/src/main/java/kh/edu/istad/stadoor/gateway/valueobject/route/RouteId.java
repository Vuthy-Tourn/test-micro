
package kh.edu.istad.stadoor.gateway.valueobject.route;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RouteId(
        UUID routeId
) {

    @NotNull
    @Override
    public String toString() {
        return routeId.toString();
    }
}
