package roomescape.theme.domain;

import org.springframework.stereotype.Component;

@Component
public class ThemeFactory {

    public Theme create(String name, String description, String imageUrl, Long storeId) {
        validate(name, description, imageUrl, storeId);
        return Theme.restore(null, name, description, imageUrl, storeId);
    }

    private void validate(String name, String description, String imageUrl, Long storeId) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("테마 이름은 필수입니다.");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("테마 설명은 필수입니다.");
        }
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new IllegalArgumentException("테마 이미지 URL은 필수입니다.");
        }
        if (storeId == null) {
            throw new IllegalArgumentException("매장 ID는 필수입니다.");
        }
    }
}