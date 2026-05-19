package roomescape.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import roomescape.exception.ErrorCode;
import roomescape.exception.business.BusinessException;
import roomescape.user.domain.User;
import roomescape.user.repository.UserRepository;

public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final UserRepository userRepository;

    public LoginMemberArgumentResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginMember.class)
                && User.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute(LoginCheckInterceptor.SESSION_KEY) == null) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        Long memberId = (Long) session.getAttribute(LoginCheckInterceptor.SESSION_KEY);
        return userRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}