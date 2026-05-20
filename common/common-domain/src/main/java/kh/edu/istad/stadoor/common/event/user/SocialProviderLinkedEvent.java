package kh.edu.istad.stadoor.common.event.user;

import kh.edu.istad.stadoor.common.entity.SocialProvider;
import kh.edu.istad.stadoor.common.valueobject.UserId;
import lombok.Builder;

@Builder
public record SocialProviderLinkedEvent(
        UserId userId,
        SocialProvider socialProvider
) {
}
