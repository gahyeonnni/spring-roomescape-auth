package roomescape.reservation.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;
import roomescape.user.domain.Role;
import roomescape.user.domain.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcReservationRepository implements ReservationRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    private final RowMapper<Reservation> rowMapper = (rs, rowNum) -> Reservation.restore(
            rs.getLong("reservation_id"),
            User.restore(
                    rs.getLong("member_id"),
                    rs.getString("member_name"),
                    rs.getString("member_email"),
                    rs.getString("member_password"),
                    Role.valueOf(rs.getString("member_role"))
            ),
            rs.getDate("date").toLocalDate(),
            ReservationTime.restore(
                    rs.getLong("time_id"),
                    rs.getTime("time_start_at").toLocalTime(),
                    rs.getTime("time_finish_at").toLocalTime()
            ),
            Theme.restore(
                    rs.getLong("theme_id"),
                    rs.getString("theme_name"),
                    rs.getString("theme_description"),
                    rs.getString("theme_image_url")
            )
    );

    public JdbcReservationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reservation")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Reservation save(Reservation reservation) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("date", reservation.getDate())
                .addValue("time_id", reservation.getTime().getId())
                .addValue("theme_id", reservation.getTheme().getId())
                .addValue("member_id", reservation.getMember().getId());
        Long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        return Reservation.restore(id, reservation.getMember(), reservation.getDate(), reservation.getTime(), reservation.getTheme());
    }

    @Override
    public Optional<Reservation> findById(Long id) {
        String query = """
                SELECT r.id as reservation_id, r.date,
                       u.id as member_id, u.name as member_name, u.email as member_email,
                       u.password as member_password, u.role as member_role,
                       rt.id as time_id, rt.start_at as time_start_at, rt.finish_at as time_finish_at,
                       t.id as theme_id, t.name as theme_name, t.description as theme_description, t.image_url as theme_image_url
                FROM reservation r
                JOIN "user" u ON r.member_id = u.id
                JOIN reservation_time rt ON r.time_id = rt.id
                JOIN theme t ON r.theme_id = t.id
                WHERE r.id = ?
                """;
        return jdbcTemplate.query(query, rowMapper, id).stream().findFirst();
    }

    @Override
    public List<Reservation> findByMemberId(Long memberId) {
        String query = """
                SELECT r.id as reservation_id, r.date,
                       u.id as member_id, u.name as member_name, u.email as member_email,
                       u.password as member_password, u.role as member_role,
                       rt.id as time_id, rt.start_at as time_start_at, rt.finish_at as time_finish_at,
                       t.id as theme_id, t.name as theme_name, t.description as theme_description, t.image_url as theme_image_url
                FROM reservation r
                JOIN "user" u ON r.member_id = u.id
                JOIN reservation_time rt ON r.time_id = rt.id
                JOIN theme t ON r.theme_id = t.id
                WHERE r.member_id = ?
                ORDER BY r.date DESC, rt.start_at DESC
                """;
        return jdbcTemplate.query(query, rowMapper, memberId);
    }

    @Override
    public void update(Long id, LocalDate date, Long timeId) {
        String query = "UPDATE reservation SET date = ?, time_id = ? WHERE id = ?";
        jdbcTemplate.update(query, date, timeId, id);
    }

    @Override
    public boolean existsByDateAndTimeIdAndThemeId(LocalDate date, Long timeId, Long themeId) {
        String query = "SELECT count(*) FROM reservation WHERE date = ? AND time_id = ? AND theme_id = ?";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, date, timeId, themeId);
        return count != null && count > 0;
    }

    @Override
    public void deleteById(Long id) {
        String query = "DELETE FROM reservation WHERE id = ?";
        jdbcTemplate.update(query, id);
    }
}
