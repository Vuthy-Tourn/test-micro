package kh.edu.istad.stadoor.common.event.user;

import kh.edu.istad.stadoor.common.entity.SocialProvider;
import kh.edu.istad.stadoor.common.valueobject.*;
import lombok.Builder;
import org.jspecify.annotations.NullMarked;

import java.time.LocalDate;

@Builder
public record UserRegisteredEvent(
        UserId userId,
        TenantId tenantId,
        RoleId roleId,
        ClientId clientId,
        SocialProvider socialProvider,
        UserNameInfo userNameInfo,
        String username,
        String email,
        String password,
        GenderType gender,
        LocalDate dob,
        AccountStatus status,
        boolean verifiedEmail,
        String phoneNumber,
        String profileImage,
        String createdBy
) {

    @NullMarked
    @Override
    public String toString() {
        return "UserRegisteredEvent[" +
                "userId=" + userId +
                ", tenantId=" + tenantId +
                ", roleId=" + roleId +
                ", clientId=" + clientId +
                ", socialProvider=" + socialProvider +
                ", userNameInfo=" + userNameInfo +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password=****" +
                ", gender=" + gender +
                ", dob=" + dob +
                ", status=" + status +
                ", verifiedEmail=" + verifiedEmail +
                ", profileImage='" + profileImage + '\'' +
                ']';
    }
}
