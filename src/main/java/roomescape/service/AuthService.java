package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.common.auth.jwt.TokenProvider;
import roomescape.common.exception.NotFoundException;
import roomescape.common.exception.UnauthorizedException;
import roomescape.dao.MemberDao;
import roomescape.domain.member.Member;
import roomescape.dto.request.LoginRequest;
import roomescape.dto.response.LoginResponse;

@Service
public class AuthService {

    private final MemberDao memberDao;
    private final TokenProvider tokenProvider;

    public AuthService(MemberDao memberDao, TokenProvider tokenProvider) {
        this.memberDao = memberDao;
        this.tokenProvider = tokenProvider;
    }

    public LoginResponse login(LoginRequest request) {
        Member member = memberDao.findByLoginId(request.loginId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 아이디입니다."));

        if (!member.getPassword().equals(request.password())) {
            throw new UnauthorizedException("비밀번호가 일치하지 않습니다.");
        }

        String token = tokenProvider.createToken(member.getId());

        return LoginResponse.from(token);
    }
}
