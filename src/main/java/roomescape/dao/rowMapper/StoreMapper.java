package roomescape.dao.rowMapper;

import org.springframework.jdbc.core.RowMapper;
import roomescape.domain.reservation.store.Store;
import roomescape.domain.reservation.store.StoreName;

public final class StoreMapper {

    public static final RowMapper<Store> STORE_ROW_MAPPER = (rs, rowNum) -> {
        return new Store(
                rs.getLong("id"),
                StoreName.parse(rs.getString("name"))
        );
    };

    private StoreMapper() {
    }
}
