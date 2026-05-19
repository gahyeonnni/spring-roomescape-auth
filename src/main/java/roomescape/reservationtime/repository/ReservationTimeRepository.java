package roomescape.reservationtime.repository;

import roomescape.reservationtime.domain.ReservationTime;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationTimeRepository {
    ReservationTime save(ReservationTime time);
    List<ReservationTime> findAll();
    Optional<ReservationTime> findById(Long id);
    List<ReservationTime> findAvailableByDateAndThemeId(LocalDate date, Long themeId);
    boolean existsReservationByTimeId(Long timeId);
    void deleteById(Long id);
}
