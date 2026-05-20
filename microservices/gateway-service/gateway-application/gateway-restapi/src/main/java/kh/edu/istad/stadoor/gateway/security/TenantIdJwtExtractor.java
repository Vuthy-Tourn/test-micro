package kh.edu.istad.stadoor.gateway.security;

import kh.edu.istad.stadoor.common.valueobject.TenantId;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TenantIdJwtExtractor {

    private static final String TENANT_ID_CLAIM = "tenant_id";

    public TenantId extract(Jwt jwt) {
        if (jwt == null) {
            return null;
        }

        String tenantIdValue = jwt.getClaimAsString(TENANT_ID_CLAIM);
        if (tenantIdValue == null || tenantIdValue.isBlank()) {
            return null; // Return null instead of throwing an exception
        }

        try {
            return new TenantId(UUID.fromString(tenantIdValue));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
