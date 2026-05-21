package roomescape.domain.reservation.store;

public class Store {
    private final Long id;
    private final StoreName name;

    public Store(Long id, StoreName name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public StoreName getName() {
        return name;
    }
}
