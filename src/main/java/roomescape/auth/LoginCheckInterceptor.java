package roomescape.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginCheckInterceptor implements HandlerInterceptor {

    static final String SESSION_KEY = "loginMemberId";
    static final String SESSION_ROLE_KEY = "loginMemberRole";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        if (handlerMethod.hasMethodAnnotation(AdminOnly.class)) {
            return checkAdmin(request, response);
        }

        if (handlerMethod.hasMethodAnnotation(LoginRequired.class)) {
            return checkLogin(request, response);
        }

        return true;
    }

    private boolean checkLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SESSION_KEY) == null) {
            writeJson(response, HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED", "로그인이 필요합니다.");
            return false;
        }
        return true;
    }

    private boolean checkAdmin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SESSION_KEY) == null) {
            writeJson(response, HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED", "로그인이 필요합니다.");
            return false;
        }
        if (!"ADMIN".equals(session.getAttribute(SESSION_ROLE_KEY))) {
            writeJson(response, HttpServletResponse.SC_FORBIDDEN, "FORBIDDEN", "관리자만 접근 가능합니다.");
            return false;
        }
        return true;
    }

    private void writeJson(HttpServletResponse response, int status, String errorCode, String message) throws Exception {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"errorCode\":\"" + errorCode + "\",\"message\":\"" + message + "\"}");
    }
}