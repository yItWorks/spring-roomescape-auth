package roomescape.common.auth.resolver;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import roomescape.common.exception.ForbiddenException;
import roomescape.common.exception.UnauthorizedException;
import roomescape.dao.ManagerDao;
import roomescape.domain.member.manager.Manager;

@Component
public class LoginManagerArgumentResolver implements HandlerMethodArgumentResolver {
    private static final String LOGIN_MEMBER_ID = "loginMemberId";

    private final ManagerDao managerDao;

    public LoginManagerArgumentResolver(ManagerDao managerDao) {
        this.managerDao = managerDao;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasAnnotation = parameter.hasParameterAnnotation(LoginManager.class);
        boolean isMemberType = Manager.class.isAssignableFrom(parameter.getParameterType());

        return hasAnnotation && isMemberType;
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        Long memberId = (Long) request.getAttribute(LOGIN_MEMBER_ID);

        if (memberId == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }

        return managerDao.findByMemberId(memberId)
                .orElseThrow(() -> new ForbiddenException("권한이 없습니다."));
    }
}
