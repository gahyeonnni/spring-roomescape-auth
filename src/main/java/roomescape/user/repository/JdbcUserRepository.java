package roomescape.user.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import roomescape.user.domain.Role;
import roomescape.user.domain.User;

import java.util.Optional;

@Repository
public class JdbcUserRepository implements UserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    private final RowMapper<User> rowMapper = (rs, rowNum) -> User.restore(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("password"),
            Role.valueOf(rs.getString("role"))
    );

    public JdbcUserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("\"user\"")
                .usingGeneratedKeyColumns("id")
                .usingColumns("name", "email", "password", "role");
    }

    @Override
    public User save(User user) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name", user.getName())
                .addValue("email", user.getEmail())
                .addValue("password", user.getPassword())
                .addValue("role", user.getRole().name());
        Long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        return User.restore(id, user.getName(), user.getEmail(), user.getPassword(), user.getRole());
    }

    @Override
    public Optional<User> findById(Long id) {
        String query = "SELECT * FROM \"user\" WHERE id = ?";
        return jdbcTemplate.query(query, rowMapper, id).stream().findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String query = "SELECT * FROM \"user\" WHERE email = ?";
        return jdbcTemplate.query(query, rowMapper, email).stream().findFirst();
    }
}
