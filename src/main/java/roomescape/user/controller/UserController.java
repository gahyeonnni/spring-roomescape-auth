package roomescape.user.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.auth.LoginMember;
import roomescape.auth.LoginRequired;
import roomescape.auth.SingleSessionRegistry;
import roomescape.user.domain.User;
import roomescape.user.dto.LoginRequest;
import roomescape.user.dto.LoginResponse;
import roomescape.user.dto.SignupRequest;
import roomescape.user.dto.SignupResponse;
import roomescape.user.service.UserService;

import java.net.URI;
import java.util.Map;

@Tag(name = "사용자", description = "회원가입·로그인·로그아웃·내 정보 조회 API")
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final SingleSessionRegistry sessionRegistry;

    public UserController(UserService userService, SingleSessionRegistry sessionRegistry) {
        this.userService = userService;
        this.sessionRegistry = sessionRegistry;
    }

    @PostMapping
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        SignupResponse response = userService.signup(request);
        return ResponseEntity.created(URI.create("/users/" + response.id())).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        User user = userService.login(request);
        sessionRegistry.register(user.getId(), session.getId());
        session.setAttribute("loginMemberId", user.getId());
        session.setAttribute("loginMemberRole", user.getRole().name());
        return ResponseEntity.ok(new LoginResponse(session.getId(), user.getRole().name()));
    }

    @LoginRequired
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@LoginMember User loginUser, HttpSession session) {
        sessionRegistry.remove(loginUser.getId());
        session.invalidate();
        return ResponseEntity.ok().build();
    }

    @LoginRequired
    @GetMapping("/my-role")
    public ResponseEntity<Map<String, String>> getMyRole(@LoginMember User loginUser) {
        return ResponseEntity.ok(Map.of("role", loginUser.getRole().name()));
    }
}
