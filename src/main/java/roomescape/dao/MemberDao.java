package roomescape.dao;

import static roomescape.dao.rowMapper.MemberMapper.MEMBER_ROW_MAPPER;

import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import roomescape.domain.member.Member;

@Repository
public class MemberDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public MemberDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("member")
                .usingGeneratedKeyColumns("id");
    }

    public Optional<Member> findByLoginId(String loginId) {
        String sql = """
                SELECT id, login_id, password, name, role
                FROM member
                WHERE login_id = ?;
                """;

        return jdbcTemplate.query(
                        sql,
                        MEMBER_ROW_MAPPER,
                        loginId
                ).stream()
                .findFirst();
    }

    public Optional<Member> findById(Long id) {
        String sql = """
                SELECT id, login_id, password, name, role
                FROM member
                WHERE id = ?;
                """;

        return jdbcTemplate.query(
                        sql,
                        MEMBER_ROW_MAPPER,
                        id
                ).stream()
                .findFirst();
    }
}
