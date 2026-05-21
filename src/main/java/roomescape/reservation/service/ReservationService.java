package roomescape.reservation.service;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.ErrorCode;
import roomescape.exception.business.BusinessException;
import roomescape.exception.business.DuplicateReservationException;
import roomescape.exception.business.ForbiddenStoreAccessException;
import roomescape.exception.business.PastTimeCancelException;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationFactory;
import roomescape.reservation.dto.ReservationRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.ReservationUpdateRequest;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.service.ReservationTimeService;
import roomescape.store.domain.Store;
import roomescape.store.repository.StoreRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.service.ThemeService;
import roomescape.user.domain.Role;
import roomescape.user.domain.User;
import roomescape.user.service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeService reservationTimeService;
    private final ThemeService themeService;
    private final ReservationFactory reservationFactory;
    private final UserService userService;
    private final StoreRepository storeRepository;

    public ReservationService(
            ReservationRepository reservationRepository,
            ReservationTimeService reservationTimeService,
            ThemeService themeService,
            ReservationFactory reservationFactory,
            UserService userService,
            StoreRepository storeRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeService = reservationTimeService;
        this.themeService = themeService;
        this.reservationFactory = reservationFactory;
        this.userService = userService;
        this.storeRepository = storeRepository;
    }

    @Transactional
    public ReservationResponse createReservation(ReservationRequest request, Long memberId) {
        User member = userService.getById(memberId);
        ReservationTime time = reservationTimeService.getById(request.timeId());
        Theme theme = themeService.getById(request.themeId());

        if (reservationRepository.existsByDateAndTimeIdAndThemeId(request.date(), request.timeId(), request.themeId())) {
            throw new DuplicateReservationException();
        }

        Reservation saved = reservationRepository.save(reservationFactory.create(member, request.date(), time, theme));
        return ReservationResponse.from(saved);
    }

    public List<ReservationResponse> getReservationsByMemberId(Long memberId) {
        return reservationRepository.findByMemberId(memberId).stream()
                .map(ReservationResponse::from)
                .collect(Collectors.toList());
    }

    public List<ReservationResponse> getReservationsByStore(Long storeId, User loginUser) {
        List<Long> managedStoreIds = storeRepository.findByManagerId(loginUser.getId()).stream()
                .map(Store::getId)
                .collect(Collectors.toList());
        if (!managedStoreIds.contains(storeId)) {
            throw new ForbiddenStoreAccessException();
        }
        return reservationRepository.findByStoreId(storeId).stream()
                .map(ReservationResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteReservation(Long id, User loginUser) {
        Reservation reservation = getById(id);
        if (reservation.isPast()) {
            throw new PastTimeCancelException();
        }
        checkStoreAccess(reservation, loginUser);
        reservationRepository.deleteById(id);
    }

    @Transactional
    public ReservationResponse updateReservation(Long id, ReservationUpdateRequest request, User loginUser) {
        Reservation reservation = getById(id);
        if (reservation.isPast()) {
            throw new BusinessException(ErrorCode.PAST_RESERVATION_UPDATE);
        }
        checkStoreAccess(reservation, loginUser);

        ReservationTime newTime = reservationTimeService.getById(request.timeId());
        LocalDate newDate = request.date();

        Reservation changed = reservation.reschedule(newDate, newTime);
        if (reservationRepository.existsByDateAndTimeIdAndThemeId(newDate, request.timeId(), reservation.getTheme().getId())) {
            throw new DuplicateReservationException();
        }

        reservationRepository.update(id, newDate, request.timeId());
        return ReservationResponse.from(changed);
    }

    private void checkStoreAccess(Reservation reservation, User loginUser) {
        if (reservation.getMember().getId().equals(loginUser.getId())) {
            return;
        }
        if (loginUser.getRole() != Role.ADMIN) {
            throw new ForbiddenStoreAccessException();
        }
        List<Long> managedStoreIds = storeRepository.findByManagerId(loginUser.getId()).stream()
                .map(Store::getId)
                .collect(Collectors.toList());
        if (!managedStoreIds.contains(reservation.getTheme().getStoreId())) {
            throw new ForbiddenStoreAccessException();
        }
    }

    @NonNull
    private Reservation getById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));
    }
}