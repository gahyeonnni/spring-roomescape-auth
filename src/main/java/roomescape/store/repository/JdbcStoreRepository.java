package roomescape.store.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import roomescape.store.domain.Store;

import java.util.List;

@Repository
public class JdbcStoreRepository implements StoreRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcStoreRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Store> findAll() {
        String query = "SELECT id, name, manager_id FROM store ORDER BY id ASC";
        return jdbcTemplate.query(query,
                (rs, rowNum) -> Store.restore(rs.getLong("id"), rs.getString("name"), rs.getLong("manager_id")));
    }

    @Override
    public List<Store> findByManagerId(Long managerId) {
        String query = "SELECT id, name, manager_id FROM store WHERE manager_id = ?";
        return jdbcTemplate.query(query,
                (rs, rowNum) -> Store.restore(rs.getLong("id"), rs.getString("name"), rs.getLong("manager_id")),
                managerId);
    }
}