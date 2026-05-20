package kh.edu.istad.stadoor.common.event.user;

import kh.edu.istad.stadoor.common.valueobject.UserId;
import lombok.Builder;
import org.jspecify.annotations.NullMarked;

@Builder
public record PasswordChangedEvent(
        UserId userId,
        String password,
        String confirmPassword
) {
    @NullMarked
    @Override
    public String toString() {
        return "PasswordChangedEvent[" +
                "userId=" + userId +
                ", password=****" +
                ", confirmPassword=****" +
                ']';
    }
}
