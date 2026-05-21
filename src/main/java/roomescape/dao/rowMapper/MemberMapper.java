package roomescape.dao.rowMapper;

import org.springframework.jdbc.core.RowMapper;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberName;
import roomescape.domain.member.Role;

public final class MemberMapper {

    public static final RowMapper<Member> MEMBER_ROW_MAPPER = (rs, rowNum) -> {
        return new Member(
                rs.getLong("id"),
                rs.getString("login_id"),
                rs.getString("password"),
                MemberName.parse(rs.getString("name")),
                Role.from(rs.getString("role"))
        );
    };

    private MemberMapper() {
    }
}
