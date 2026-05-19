package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.auth.LoginMember;
import roomescape.auth.LoginRequired;
import roomescape.reservation.dto.ReservationRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.ReservationUpdateRequest;
import roomescape.reservation.service.ReservationService;
import roomescape.user.domain.User;

import java.net.URI;
import java.util.List;

@Tag(name = "예약", description = "예약 생성·조회·수정·삭제 API")
@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @LoginRequired
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getMyReservations(@LoginMember User loginUser) {
        return ResponseEntity.ok(reservationService.getReservationsByMemberId(loginUser.getId()));
    }

    @LoginRequired
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationRequest request, @LoginMember User loginUser) {
        ReservationResponse response = reservationService.createReservation(request, loginUser.getId());
        return ResponseEntity.created(URI.create("/reservations/" + response.id())).body(response);
    }

    @LoginRequired
    @PatchMapping("/{id}")
    public ResponseEntity<ReservationResponse> updateReservation(
            @PathVariable Long id,
            @Valid @RequestBody ReservationUpdateRequest request
    ) {
        return ResponseEntity.ok(reservationService.updateReservation(id, request));
    }

    @LoginRequired
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}
