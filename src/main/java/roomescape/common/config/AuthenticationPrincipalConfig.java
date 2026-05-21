package roomescape.common.config;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.common.auth.interceptor.LoginCheckInterceptor;
import roomescape.common.auth.resolver.LoginManagerArgumentResolver;
import roomescape.common.auth.resolver.LoginMemberArgumentResolver;

@Configuration
public class AuthenticationPrincipalConfig implements WebMvcConfigurer {

    private final LoginCheckInterceptor loginCheckInterceptor;
    private final LoginMemberArgumentResolver loginMemberArgumentResolver;
    private final LoginManagerArgumentResolver loginManagerArgumentResolver;

    public AuthenticationPrincipalConfig(
            LoginCheckInterceptor loginCheckInterceptor,
            LoginMemberArgumentResolver loginMemberArgumentResolver,
            LoginManagerArgumentResolver loginManagerArgumentResolver
    ) {
        this.loginCheckInterceptor = loginCheckInterceptor;
        this.loginMemberArgumentResolver = loginMemberArgumentResolver;
        this.loginManagerArgumentResolver = loginManagerArgumentResolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginCheckInterceptor)
                .addPathPatterns("/reservations/**", "/admin/**")
                .excludePathPatterns(
                        "/login",
                        "/logout",
                        "/themes/popular"
                );
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginMemberArgumentResolver);
        resolvers.add(loginManagerArgumentResolver);
    }
}
