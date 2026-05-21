package roomescape.store.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.AdminOnly;
import roomescape.auth.LoginMember;
import roomescape.store.dto.StoreResponse;
import roomescape.store.repository.StoreRepository;
import roomescape.user.domain.User;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "매장", description = "매장 목록 조회 API")
@RestController
public class StoreController {

    private final StoreRepository storeRepository;

    public StoreController(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @GetMapping("/stores")
    public ResponseEntity<List<StoreResponse>> getAllStores() {
        List<StoreResponse> stores = storeRepository.findAll().stream()
                .map(StoreResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(stores);
    }

    @AdminOnly
    @GetMapping("/admin/stores")
    public ResponseEntity<List<StoreResponse>> getManagedStores(@LoginMember User loginUser) {
        List<StoreResponse> stores = storeRepository.findByManagerId(loginUser.getId()).stream()
                .map(StoreResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(stores);
    }
}