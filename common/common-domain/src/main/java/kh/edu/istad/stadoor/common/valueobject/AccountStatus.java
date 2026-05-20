package kh.edu.istad.stadoor.common.valueobject;

import lombok.Builder;

@Builder
public record AccountStatus (
        Boolean accountNonExpired,
        Boolean accountNonLocked,
        Boolean credentialsNonExpired,
        Boolean isActive

){}