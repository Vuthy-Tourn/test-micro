
package kh.edu.istad.stadoor.gateway.valueobject.service;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;
public record ServiceId(
        UUID serviceId
) {
    @NotNull
    @Override
    public String toString() {
        return serviceId.toString();
    }

}
