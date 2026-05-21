package roomescape.domain.member;

import java.util.List;
import java.util.Objects;

public class Manager {
    private final Long id;
    private final Member member;
    private final List<Store> stores;

    public Manager(Long id, Member member, List<Store> stores) {
        this.id = id;
        validate(member);
        this.member = member;
        this.stores = stores;
    }

    private void validate(Member member) {
        Objects.requireNonNull(member, "회원 정보가 없습니다.");
    }

    public Member getMember() {
        return member;
    }

    public List<Store> getStores() {
        return stores;
    }
}
