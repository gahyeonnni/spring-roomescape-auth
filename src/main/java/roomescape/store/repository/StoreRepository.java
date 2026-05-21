package roomescape.store.repository;

import roomescape.store.domain.Store;

import java.util.List;

public interface StoreRepository {
    List<Store> findAll();
    List<Store> findByManagerId(Long managerId);
}