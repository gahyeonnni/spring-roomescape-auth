package roomescape.reservation.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.exception.business.BusinessException;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;
import roomescape.user.domain.Role;
import roomescape.user.domain.User;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.*;

class ReservationTest {

    private final ReservationFactory factory = new ReservationFactory();
    private final ReservationTime time = ReservationTime.restore(1L, LocalTime.of(10, 0), LocalTime.of(11, 0));
    private final Theme theme = Theme.restore(1L, "테마1", "설명", "https://image.com", 1L);
    private final User member = User.restore(1L, "현미밥", "test@test.com", "password", Role.USER);
    private final LocalDate futureDate = LocalDate.now().plusDays(1);

    @Test
    @DisplayName("정상 예약 생성")
    void 정상_예약_생성() {
        assertThatCode(() -> factory.create(member, futureDate, time, theme))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("예약자가 null이면 예외 발생")
    void 예약자_null_예외() {
        assertThatThrownBy(() -> factory.create(null, futureDate, time, theme))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("예약자는 필수입니다.");
    }

    @Test
    @DisplayName("날짜가 null이면 예외 발생")
    void 날짜_null_예외() {
        assertThatThrownBy(() -> factory.create(member, null, time, theme))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("날짜는 필수입니다.");
    }

    @Test
    @DisplayName("과거 날짜면 예외 발생")
    void 과거_날짜_예외() {
        assertThatThrownBy(() -> factory.create(member, LocalDate.now().minusDays(1), time, theme))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("시간이 null이면 예외 발생")
    void 시간_null_예외() {
        assertThatThrownBy(() -> factory.create(member, futureDate, null, theme))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("예약 시간은 필수입니다.");
    }

    @Test
    @DisplayName("테마가 null이면 예외 발생")
    void 테마_null_예외() {
        assertThatThrownBy(() -> factory.create(member, futureDate, time, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("테마는 필수입니다.");
    }

    @Test
    @DisplayName("과거 예약이면 isPast가 true를 반환한다")
    void isPast_과거면_true() {
        Reservation past = Reservation.restore(1L, member, LocalDate.now().minusDays(1), time, theme);
        assertThat(past.isPast()).isTrue();
    }

    @Test
    @DisplayName("미래 예약이면 isPast가 false를 반환한다")
    void isPast_미래면_false() {
        Reservation future = Reservation.restore(1L, member, LocalDate.now().plusDays(1), time, theme);
        assertThat(future.isPast()).isFalse();
    }
}
