package roomescape.reservation.repository;

import roomescape.reservation.domain.Reservation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository {
    Reservation save(Reservation reservation);
    Optional<Reservation> findById(Long id);
    List<Reservation> findByMemberId(Long memberId);
    void update(Long id, LocalDate date, Long timeId);
    boolean existsByDateAndTimeIdAndThemeId(LocalDate date, Long timeId, Long themeId);
    void deleteById(Long id);
}
