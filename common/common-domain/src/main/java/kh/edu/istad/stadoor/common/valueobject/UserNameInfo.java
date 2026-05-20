package kh.edu.istad.stadoor.common.valueobject;

import lombok.Builder;

@Builder
public record UserNameInfo(String firstName, String lastName) {
}
