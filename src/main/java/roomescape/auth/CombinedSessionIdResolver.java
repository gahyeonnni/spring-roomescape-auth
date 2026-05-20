package roomescape.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.session.web.http.HttpSessionIdResolver;

import java.util.Collections;
import java.util.List;

public class CombinedSessionIdResolver implements HttpSessionIdResolver {

    public static final String SESSION_HEADER = "X-Session-Id";

    @Override
    public List<String> resolveSessionIds(HttpServletRequest request) {
        String sessionId = request.getHeader(SESSION_HEADER);
        if (sessionId != null && !sessionId.isBlank()) {
            return List.of(sessionId);
        }
        return Collections.emptyList();
    }

    @Override
    public void setSessionId(HttpServletRequest request, HttpServletResponse response, String sessionId) {
        response.setHeader(SESSION_HEADER, sessionId);
    }

    @Override
    public void expireSession(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader(SESSION_HEADER, "");
    }
}