package roomescape.theme.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.auth.AdminOnly;
import roomescape.auth.LoginMember;
import roomescape.theme.dto.AdminThemeRequest;
import roomescape.theme.dto.AdminThemeResponse;
import roomescape.theme.service.AdminThemeService;
import roomescape.user.domain.User;

import java.net.URI;
import java.util.List;

@Tag(name = "어드민 테마", description = "테마 생성·조회·삭제 API (관리자용)")
@RestController
@RequestMapping("/admin/themes")
public class AdminThemeController {

    private final AdminThemeService adminThemeService;

    public AdminThemeController(AdminThemeService adminThemeService) {
        this.adminThemeService = adminThemeService;
    }

    @AdminOnly
    @PostMapping
    public ResponseEntity<AdminThemeResponse> createTheme(
            @Valid @RequestBody AdminThemeRequest request,
            @LoginMember User loginUser) {
        AdminThemeResponse response = adminThemeService.createTheme(request, loginUser);
        return ResponseEntity.created(URI.create("/admin/themes/" + response.id())).body(response);
    }

    @AdminOnly
    @GetMapping
    public ResponseEntity<List<AdminThemeResponse>> getThemes(@LoginMember User loginUser) {
        return ResponseEntity.ok(adminThemeService.getAllThemes(loginUser));
    }

    @AdminOnly
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheme(@PathVariable Long id, @LoginMember User loginUser) {
        adminThemeService.deleteTheme(id, loginUser);
        return ResponseEntity.noContent().build();
    }
}