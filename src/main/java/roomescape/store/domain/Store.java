package roomescape.store.domain;

public class Store {
    private final Long id;
    private final String name;
    private final Long managerId;

    private Store(Long id, String name, Long managerId) {
        this.id = id;
        this.name = name;
        this.managerId = managerId;
    }

    public static Store restore(Long id, String name, Long managerId) {
        return new Store(id, name, managerId);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getManagerId() {
        return managerId;
    }
}