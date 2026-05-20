package kh.edu.istad.stadoor.common.event.user;

import kh.edu.istad.stadoor.common.valueobject.UserId;
import lombok.Builder;
import org.jspecify.annotations.NullMarked;

import java.time.LocalDateTime;

@Builder
public record PersonalAccessTokenCreatedEvent(
        UserId userId,
        String tokenId,
        String name,
        String tokenHash,
        LocalDateTime createdAt,
        LocalDateTime expiresAt,
        String createdBy
) {

    @NullMarked
    @Override
    public String toString() {
        return "PersonalAccessTokenCreatedEvent[" +
                "userId=" + userId +
                ", tokenId='" + tokenId + '\'' +
                ", userNameInfo='" + name + '\'' +
                ", tokenHash=****" +
                ", createdAt=" + createdAt +
                ", expiresAt=" + expiresAt +
                ", createdBy='" + createdBy + '\'' +
                ']';
    }
}
