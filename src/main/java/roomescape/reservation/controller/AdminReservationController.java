package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.AdminOnly;
import roomescape.auth.LoginMember;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.service.ReservationService;
import roomescape.user.domain.User;

import java.util.List;

@Tag(name = "어드민 예약", description = "매장별 예약 조회 API (관리자용)")
@RestController
@RequestMapping("/admin/reservations")
public class AdminReservationController {

    private final ReservationService reservationService;

    public AdminReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @AdminOnly
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getStoreReservations(
            @RequestParam Long storeId,
            @LoginMember User loginUser) {
        return ResponseEntity.ok(reservationService.getReservationsByStore(storeId, loginUser));
    }
}