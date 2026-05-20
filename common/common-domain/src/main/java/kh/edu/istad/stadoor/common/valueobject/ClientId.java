package kh.edu.istad.stadoor.common.valueobject;

import org.jspecify.annotations.NullMarked;

public record ClientId(String id) {

    @NullMarked
    @Override
    public String toString() {
        return id;
    }
}
