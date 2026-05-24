package roomescape.auth;

import org.springframework.stereotype.Component;
import roomescape.exception.ErrorCode;
import roomescape.exception.business.BusinessException;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class SingleSessionRegistry {

    private final ConcurrentHashMap<Long, String> memberToSession = new ConcurrentHashMap<>();

    public void register(Long memberId, String sessionId) {
        String existing = memberToSession.putIfAbsent(memberId, sessionId);
        if (existing != null) {
            throw new BusinessException(ErrorCode.ALREADY_LOGGED_IN);
        }
    }

    public void remove(Long memberId) {
        memberToSession.remove(memberId);
    }
}