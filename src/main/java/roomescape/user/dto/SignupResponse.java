package roomescape.user.dto;

import roomescape.user.domain.User;

public record SignupResponse(Long id, String name, String email) {

    public static SignupResponse from(User user) {
        return new SignupResponse(user.getId(), user.getName(), user.getEmail());
    }
}