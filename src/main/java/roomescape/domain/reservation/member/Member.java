package roomescape.domain.reservation.member;

import java.util.Objects;

public class Member {
    private final Long id;
    private final String loginId;
    private final String password;
    private final MemberName name;

    public Member(Long id, String loginId, String password, MemberName name) {
        this.id = id;
        validate(loginId, password, name);
        this.loginId = loginId;
        this.password = password;
        this.name = name;
    }

    private void validate(String loginId, String password, MemberName name) {
        Objects.requireNonNull(loginId, "ID가 비어 있습니다.");
        Objects.requireNonNull(password, "비밀번호가 비어 있습니다.");
        Objects.requireNonNull(name, "예약자 이름이 비어 있습니다.");
    }

    public Long getId() {
        return id;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getPassword() {
        return password;
    }

    public MemberName getName() {
        return name;
    }
}
