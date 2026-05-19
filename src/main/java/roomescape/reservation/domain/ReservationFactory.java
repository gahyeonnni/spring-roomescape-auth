package roomescape.reservation.domain;

import org.springframework.stereotype.Component;
import roomescape.exception.ErrorCode;
import roomescape.exception.business.BusinessException;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;
import roomescape.user.domain.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class ReservationFactory {

    public Reservation create(User member, LocalDate date, ReservationTime time, Theme theme) {
        validate(member, date, time, theme);
        return Reservation.restore(null, member, date, time, theme);
    }

    private void validate(User member, LocalDate date, ReservationTime time, Theme theme) {
        if (member == null) {
            throw new IllegalArgumentException("예약자는 필수입니다.");
        }
        if (date == null) {
            throw new IllegalArgumentException("날짜는 필수입니다.");
        }
        if (time == null || time.getId() == null) {
            throw new IllegalArgumentException("예약 시간은 필수입니다.");
        }
        if (LocalDateTime.of(date, time.getStartAt()).isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.PAST_TIME_CREATE);
        }
        if (theme == null) {
            throw new IllegalArgumentException("테마는 필수입니다.");
        }
    }
}
