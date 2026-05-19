package roomescape.reservationtime.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.auth.AdminOnly;
import roomescape.auth.LoginRequired;
import roomescape.reservationtime.dto.TimeRequest;
import roomescape.reservationtime.dto.TimeResponse;
import roomescape.reservationtime.service.ReservationTimeService;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "예약 시간", description = "예약 시간 생성·조회·삭제 API")
@RestController
@RequestMapping("/times")
public class ReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @AdminOnly
    @PostMapping
    public ResponseEntity<TimeResponse> createTime(@Valid @RequestBody TimeRequest request) {
        TimeResponse response = reservationTimeService.createTime(request);
        return ResponseEntity.created(URI.create("/times/" + response.id())).body(response);
    }

    @LoginRequired
    @GetMapping
    public ResponseEntity<List<TimeResponse>> getTimes() {
        return ResponseEntity.ok(reservationTimeService.getAllTimes());
    }

    @GetMapping("/available")
    public ResponseEntity<List<TimeResponse>> getAvailableTimes(
            @RequestParam LocalDate date, @RequestParam Long themeId
    ) {
        return ResponseEntity.ok(reservationTimeService.getAvailableTimes(date, themeId));
    }

    @AdminOnly
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTime(@PathVariable Long id) {
        reservationTimeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
