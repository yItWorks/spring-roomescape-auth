package roomescape.domain.member.manager;

import java.util.Objects;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.store.Store;

public class Manager {
    private final Long id;
    private final Member member;
    private final Store store;

    public Manager(Long id, Member member, Store store) {
        this.id = id;
        validate(member);
        this.member = member;
        this.store = store;
    }

    private void validate(Member member) {
        Objects.requireNonNull(member, "회원 정보가 없습니다.");
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public Store getStore() {
        return store;
    }
}
