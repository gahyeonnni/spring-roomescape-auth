package roomescape.theme.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.ErrorCode;
import roomescape.exception.business.BusinessException;
import roomescape.exception.business.ForbiddenStoreAccessException;
import roomescape.store.domain.Store;
import roomescape.store.repository.StoreRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeFactory;
import roomescape.theme.dto.AdminThemeRequest;
import roomescape.theme.dto.AdminThemeResponse;
import roomescape.theme.repository.ThemeRepository;
import roomescape.user.domain.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AdminThemeService {

    private final ThemeRepository themeRepository;
    private final ThemeFactory themeFactory;
    private final StoreRepository storeRepository;

    public AdminThemeService(ThemeRepository themeRepository, ThemeFactory themeFactory, StoreRepository storeRepository) {
        this.themeRepository = themeRepository;
        this.themeFactory = themeFactory;
        this.storeRepository = storeRepository;
    }

    @Transactional
    public AdminThemeResponse createTheme(AdminThemeRequest request, User loginUser) {
        List<Long> managedStoreIds = getManagedStoreIds(loginUser);
        if (!managedStoreIds.contains(request.storeId())) {
            throw new ForbiddenStoreAccessException();
        }
        Theme theme = themeFactory.create(request.name(), request.description(), request.imageUrl(), request.storeId());
        Theme saved = themeRepository.save(theme);
        return AdminThemeResponse.from(saved);
    }

    public List<AdminThemeResponse> getAllThemes(User loginUser) {
        List<Long> managedStoreIds = getManagedStoreIds(loginUser);
        return themeRepository.findAllByStoreIds(managedStoreIds).stream()
                .map(AdminThemeResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteTheme(Long id, User loginUser) {
        Theme theme = themeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.THEME_NOT_FOUND));
        List<Long> managedStoreIds = getManagedStoreIds(loginUser);
        if (!managedStoreIds.contains(theme.getStoreId())) {
            throw new ForbiddenStoreAccessException();
        }
        if (themeRepository.existsReservationByThemeId(id)) {
            throw new BusinessException(ErrorCode.THEME_HAS_RESERVATION);
        }
        themeRepository.deleteById(id);
    }

    private List<Long> getManagedStoreIds(User loginUser) {
        return storeRepository.findByManagerId(loginUser.getId()).stream()
                .map(Store::getId)
                .collect(Collectors.toList());
    }
}