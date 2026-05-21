package roomescape.dao.rowMapper;

import org.springframework.jdbc.core.RowMapper;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberName;
import roomescape.domain.member.Role;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.store.Store;
import roomescape.domain.reservation.store.StoreName;
import roomescape.domain.reservation.theme.Description;
import roomescape.domain.reservation.theme.Theme;
import roomescape.domain.reservation.theme.ThemeName;
import roomescape.domain.reservation.theme.ThumbnailUrl;
import roomescape.domain.reservation.time.ReservationTime;

public final class ReservationMapper {

    public static final RowMapper<Reservation> RESERVATION_ROW_MAPPER = (rs, rowNum) -> {
        ReservationTime time = new ReservationTime(
                rs.getLong("time_id"),
                rs.getTime("start_at").toLocalTime()
        );
        Theme theme = new Theme(
                rs.getLong("theme_id"),
                ThemeName.parse(rs.getString("theme_name")),
                Description.parse(rs.getString("description")),
                ThumbnailUrl.parse(rs.getString("url"))
        );
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
        return new Reservation(
                rs.getLong("id"),
                rs.getDate("date").toLocalDate(),
                time,
                theme,
                member,
                store
        );
    };

    private ReservationMapper() {
    }
}
