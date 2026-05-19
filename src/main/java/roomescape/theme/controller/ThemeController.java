package roomescape.theme.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.service.ThemeService;

import java.util.List;

@Tag(name = "테마", description = "인기 테마 조회 API")
@RestController
@RequestMapping("/themes")
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @GetMapping
    public ResponseEntity<List<ThemeResponse>> getAllThemes() {
        return ResponseEntity.ok(themeService.getAllThemes());
    }

    @GetMapping("/top/{limit}")
    public ResponseEntity<List<ThemeResponse>> getTopThemes(@PathVariable int limit) {
        return ResponseEntity.ok(themeService.getTopThemes(limit));
    }
}
