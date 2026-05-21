package roomescape.dao;

import static roomescape.dao.rowMapper.ManagerMapper.MANAGER_ROW_MAPPER;

import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import roomescape.domain.member.manager.Manager;

@Repository
public class ManagerDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public ManagerDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("manager")
                .usingGeneratedKeyColumns("id");
    }

    public Optional<Manager> findByMemberId(Long memberId) {
        String sql = """
                SELECT m.id,
                    mem.id AS member_id, mem.login_id, mem.password, mem.name AS member_name, mem.role,
                    s.id AS store_id, s.name AS store_name
                FROM manager m
                INNER JOIN member mem ON m.member_id = mem.id
                INNER JOIN store s ON m.store_id = s.id
                WHERE m.member_id = ?;
                """;

        return jdbcTemplate.query(
                        sql,
                        MANAGER_ROW_MAPPER,
                        memberId
                ).stream()
                .findFirst();
    }
}
