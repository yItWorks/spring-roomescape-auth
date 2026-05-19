package roomescape.common.auth.resolver;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import roomescape.common.exception.UnauthorizedException;
import roomescape.dao.MemberDao;
import roomescape.domain.reservation.member.Member;

@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {
    private static final String LOGIN_MEMBER_ID = "loginMemberId";

    private final MemberDao memberDao;

    public LoginMemberArgumentResolver(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasAnnotation = parameter.hasParameterAnnotation(LoginMember.class);
        boolean isMemberType = Member.class.isAssignableFrom(parameter.getParameterType());

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
            throw new UnauthorizedException("");
        }

        return memberDao.findById(memberId)
                .orElseThrow(() -> new UnauthorizedException("ID가 틀렸습니다."));
    }
}
