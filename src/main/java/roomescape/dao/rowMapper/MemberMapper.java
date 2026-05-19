package roomescape.dao.rowMapper;

import org.springframework.jdbc.core.RowMapper;
import roomescape.domain.reservation.member.Member;
import roomescape.domain.reservation.member.MemberName;

public final class MemberMapper {

    public static final RowMapper<Member> MEMBER_ROW_MAPPER = (rs, rowNum) -> {
        return new Member(
                rs.getLong("id"),
                rs.getString("login_id"),
                rs.getString("password"),
                MemberName.parse(rs.getString("name"))
        );
    };

    private MemberMapper() {
    }
}
