package roomescape.dao;

import static roomescape.dao.rowMapper.ReservationMapper.RESERVATION_ROW_MAPPER;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.theme.Theme;
import roomescape.domain.reservation.time.ReservationTime;

@Repository
public class ReservationDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public ReservationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reservation")
                .usingGeneratedKeyColumns("id");
    }

    public List<Reservation> findAll() {
        return jdbcTemplate.query(
                """
                SELECT r.id, r.date, rt.id AS time_id, rt.start_at,
                    t.id AS theme_id, t.name AS theme_name, t.description, t.url,
                    m.id AS member_id, m.login_id, m.password, m.name AS member_name, m.role,
                    s.id AS store_id, s.name AS store_name
                FROM reservation r
                INNER JOIN reservation_time rt ON r.time_id = rt.id
                INNER JOIN theme t ON r.theme_id = t.id
                INNER JOIN member m ON m.id = r.member_id
                INNER JOIN store s ON s.id = r.store_id;
            """,
                RESERVATION_ROW_MAPPER
        );
    }

    public List<Reservation> findAllByMemberId(Long memberId) {
        String sql = """
                SELECT r.id, r.date,rt.id AS time_id, rt.start_at,
                    t.id AS theme_id, t.name AS theme_name, t.description, t.url,
                    m.id AS member_id, m.login_id, m.password, m.name AS member_name, m.role,
                    s.id AS store_id, s.name AS store_name
                FROM reservation r
                INNER JOIN reservation_time rt ON r.time_id = rt.id
                INNER JOIN theme t ON r.theme_id = t.id
                INNER JOIN member m ON m.id = r.member_id
                INNER JOIN store s ON s.id = r.store_id
                WHERE r.member_id = ?;
                """;
        return jdbcTemplate.query(
                sql,
                RESERVATION_ROW_MAPPER,
                memberId
        );
    }

    public List<Reservation> findAllByStoreId(Long storeId) {
        String sql = """
                SELECT r.id, r.date,rt.id AS time_id, rt.start_at,
                    t.id AS theme_id, t.name AS theme_name, t.description, t.url,
                    m.id AS member_id, m.login_id, m.password, m.name AS member_name, m.role,
                    s.id AS store_id, s.name AS store_name
                FROM reservation r
                INNER JOIN reservation_time rt ON r.time_id = rt.id
                INNER JOIN theme t ON r.theme_id = t.id
                INNER JOIN member m ON m.id = r.member_id
                INNER JOIN store s ON s.id = r.store_id
                WHERE r.store_id = ?;
                """;
        return jdbcTemplate.query(
                sql,
                RESERVATION_ROW_MAPPER,
                storeId
        );
    }

    public boolean existsBy(LocalDate date, Theme theme, ReservationTime time) {
        Boolean result = jdbcTemplate.queryForObject("""
                        SELECT EXISTS(
                            SELECT 1
                            FROM reservation
                            WHERE date = ?
                                AND time_id = ?
                                AND theme_id = ?
                        ) 
                        """,
                Boolean.class,
                date,
                time.getId(),
                theme.getId()
        );
        return Boolean.TRUE.equals(result);
    }

    public Reservation save(Reservation reservation) {
        Map<String, Object> params = new HashMap<>();
        params.put("date", reservation.getDate());
        params.put("time_id", reservation.getTime().getId());
        params.put("theme_id", reservation.getTheme().getId());
        params.put("member_id", reservation.getMember().getId());
        params.put("store_id", reservation.getStore().getId());

        Long id = jdbcInsert.executeAndReturnKey(params).longValue();
        return new Reservation(
                id,
                reservation.getDate(),
                reservation.getTime(),
                reservation.getTheme(),
                reservation.getMember(),
                reservation.getStore()
        );
    }

    public boolean update(Reservation reservation) {
        String sql = """
                UPDATE reservation
                SET date = ?, time_id = ?, theme_id = ?, member_id = ?, store_id = ?
                WHERE id = ?;
                """;

        int affectedRows = jdbcTemplate.update(
                sql,
                reservation.getDate(),
                reservation.getTime().getId(),
                reservation.getTheme().getId(),
                reservation.getMember().getId(),
                reservation.getStore().getId(),
                reservation.getId()
        );

        return affectedRows > 0;
    }

    public boolean existsById(Long id) {
        String sql = """
                SELECT EXISTS(
                    SELECT 1
                    FROM reservation
                    WHERE id = ?
                )
                """;

        Boolean result = jdbcTemplate.queryForObject(
                sql,
                Boolean.class,
                id
        );

        return Boolean.TRUE.equals(result);
    }

    public boolean existsByTimeId(Long timeId) {
        String sql = """
                SELECT EXISTS(
                    SELECT 1
                    FROM reservation
                    WHERE time_id = ?
                )
                """;

        Boolean result = jdbcTemplate.queryForObject(
                sql,
                Boolean.class,
                timeId
        );
        return Boolean.TRUE.equals(result);
    }

    public Optional<Reservation> findById(Long id) {
        String sql = """
                SELECT r.id, r.date,rt.id AS time_id, rt.start_at,
                    t.id AS theme_id, t.name AS theme_name, t.description, t.url,
                    m.id AS member_id, m.login_id, m.password, m.name AS member_name, m.role,
                    s.id AS store_id, s.name AS store_name
                FROM reservation r
                INNER JOIN reservation_time rt ON r.time_id = rt.id
                INNER JOIN theme t ON r.theme_id = t.id
                INNER JOIN member m ON m.id = r.member_id
                INNER JOIN store s ON s.id = r.store_id
                WHERE r.id = ?
                """;

        return jdbcTemplate.query(
                        sql,
                        RESERVATION_ROW_MAPPER,
                        id
                ).stream()
                .findFirst();
    }

    public void deleteById(Long id) {
        String sql = """
                DELETE FROM reservation 
                WHERE id = ?
                """;

        jdbcTemplate.update(
                sql,
                id
        );
    }
}
