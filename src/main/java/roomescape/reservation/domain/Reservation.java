package roomescape.reservation.domain;

import lombok.Builder;
import roomescape.exception.ErrorCode;
import roomescape.exception.business.BusinessException;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;
import roomescape.user.domain.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Reservation {

    private final Long id;
    private final User member;
    private final LocalDate date;
    private final ReservationTime time;
    private final Theme theme;

    @Builder(access = lombok.AccessLevel.PRIVATE)
    private Reservation(Long id, User member, LocalDate date, ReservationTime time, Theme theme) {
        this.id = id;
        this.member = member;
        this.date = date;
        this.time = time;
        this.theme = theme;
    }

    public static Reservation restore(Long id, User member, LocalDate date, ReservationTime time, Theme theme) {
        return Reservation.builder()
                .id(id).member(member).date(date).time(time).theme(theme)
                .build();
    }

    public Reservation reschedule(LocalDate date, ReservationTime time) {
        Reservation changed = Reservation.builder()
                .id(id)
                .member(this.member)
                .date(date)
                .time(time)
                .theme(this.theme)
                .build();
        if (changed.isPast()) {
            throw new BusinessException(ErrorCode.PAST_TIME_RESERVATION);
        }
        return changed;
    }

    public boolean isPast() {
        return LocalDateTime.of(date, time.getStartAt()).isBefore(LocalDateTime.now());
    }

    public Long getId() { return id; }
    public User getMember() { return member; }
    public LocalDate getDate() { return date; }
    public ReservationTime getTime() { return time; }
    public Theme getTheme() { return theme; }
}
