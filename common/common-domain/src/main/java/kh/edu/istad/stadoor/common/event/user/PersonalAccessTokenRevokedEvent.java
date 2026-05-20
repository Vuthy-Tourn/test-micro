package kh.edu.istad.stadoor.common.event.user;

import kh.edu.istad.stadoor.common.valueobject.UserId;
import lombok.Builder;

@Builder
public record PersonalAccessTokenRevokedEvent(
        UserId userId,
        String tokenId,
        String updatedBy
) {
}
