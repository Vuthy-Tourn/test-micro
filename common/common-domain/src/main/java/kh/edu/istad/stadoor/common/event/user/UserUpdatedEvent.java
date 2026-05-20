package kh.edu.istad.stadoor.common.event.user;


import kh.edu.istad.stadoor.common.valueobject.UserId;
import kh.edu.istad.stadoor.common.valueobject.UserNameInfo;
import lombok.Builder;

@Builder
public record UserUpdatedEvent(
        UserId userId,
        String username,
        UserNameInfo userNameInfo,
        String phoneNumber,
        String profileImage,
        String updatedBy
) {
}
