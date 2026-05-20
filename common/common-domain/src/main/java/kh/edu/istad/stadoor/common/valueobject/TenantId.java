package kh.edu.istad.stadoor.common.valueobject;

import org.jspecify.annotations.NullMarked;

import java.util.UUID;

public record TenantId(UUID id) {

    @NullMarked
    @Override
    public String toString() {
        return id.toString();
    }
}
