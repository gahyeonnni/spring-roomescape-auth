package roomescape.user.domain;

import org.springframework.stereotype.Component;

@Component
public class UserFactory {
    public User create(String name, String email, String password, Role role) {
        validate(name, email, password, role);
        return User.restore(null, name, email, password, role);
    }

    private void validate(String name, String email, String password, Role role) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일은 필수입니다.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
        if (role == null) {
            throw new IllegalArgumentException("역할은 필수입니다.");
        }
    }
}
