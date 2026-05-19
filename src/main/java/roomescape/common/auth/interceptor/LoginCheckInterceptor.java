package roomescape.common.auth.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.common.auth.jwt.TokenProvider;

@Component
public class LoginCheckInterceptor implements HandlerInterceptor {
    private static final String LOGIN_MEMBER_ID = "loginMemberId";

    private final TokenProvider tokenProvider;

    public LoginCheckInterceptor(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {
        String token = resolveToken(request);

        if (!(StringUtils.hasText(token) && tokenProvider.isValidToken(token))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        Long userId = tokenProvider.getUserId(token);
        request.setAttribute(LOGIN_MEMBER_ID, userId);

        return true;
    }

    // "Authorization: Bearer <token>" 형태에서 순수 토큰만 추출하는 유틸 메서드
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
