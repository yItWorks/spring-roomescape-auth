package roomescape.dao;

import static roomescape.dao.rowMapper.StoreMapper.STORE_ROW_MAPPER;

import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import roomescape.domain.reservation.store.Store;

@Repository
public class StoreDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public StoreDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("store")
                .usingGeneratedKeyColumns("id");
    }

    public Optional<Store> findById(Long id) {
        String sql = """
                SELECT id, name 
                FROM store
                WHERE id = ?
                """;

        return jdbcTemplate.query(
                        sql,
                        STORE_ROW_MAPPER,
                        id
                ).stream()
                .findFirst();
    }
}
