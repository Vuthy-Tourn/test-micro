package kh.edu.istad.stadoor.common.entity;

import kh.edu.istad.stadoor.common.valueobject.SocialProviderType;
import kh.edu.istad.stadoor.common.valueobject.UserId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.axonframework.modelling.command.EntityId;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SocialProvider {

    @EntityId
    private SocialProviderType providerType;
    private UserId providerUserId;
    private String providerEmail;
    private LocalDate createdAt;
}
