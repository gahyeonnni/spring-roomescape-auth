package roomescape.page;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String root() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/reservation")
    public String reservation() {
        return "reservation";
    }

    @GetMapping("/time")
    public String time(HttpServletRequest request) {
        return requireAdmin(request, "time");
    }

    @GetMapping("/theme")
    public String theme(HttpServletRequest request) {
        return requireAdmin(request, "theme");
    }

    @GetMapping("/popular")
    public String popular() {
        return "popular";
    }

    @GetMapping("/my-reservations")
    public String myReservations(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginMemberId") == null) {
            return "redirect:/login";
        }
        return "my-reservations";
    }

    private String requireAdmin(HttpServletRequest request, String template) {
        HttpSession session = request.getSession(false);
        if (session == null || !"ADMIN".equals(session.getAttribute("loginMemberRole"))) {
            return "redirect:/home";
        }
        return template;
    }
}