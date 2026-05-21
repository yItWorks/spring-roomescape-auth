package roomescape.dao.rowMapper;

import org.springframework.jdbc.core.RowMapper;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberName;
import roomescape.domain.member.Role;
import roomescape.domain.member.manager.Manager;
import roomescape.domain.reservation.store.Store;
import roomescape.domain.reservation.store.StoreName;

public final class ManagerMapper {

    public static final RowMapper<Manager> MANAGER_ROW_MAPPER = (rs, rowNum) -> {
        Member member = new Member(
                rs.getLong("member_id"),
                rs.getString("login_id"),
                rs.getString("password"),
                MemberName.parse(rs.getString("member_name")),
                Role.from(rs.getString("role"))
        );
        Store store = new Store(
                rs.getLong("store_id"),
                StoreName.parse(rs.getString("store_name"))
        );
        return new Manager(
                rs.getLong("id"),
                member,
                store
        );
    };

    private ManagerMapper() {
    }
}
