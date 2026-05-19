package roomescape.reservationtime.dto;

import roomescape.reservationtime.domain.ReservationTime;

import java.time.LocalTime;

public record TimeResponse(
        Long id,
        LocalTime startAt
) {

    public static TimeResponse of(ReservationTime saved) {
        return new TimeResponse(saved.getId(), saved.getStartAt());
    }
}
