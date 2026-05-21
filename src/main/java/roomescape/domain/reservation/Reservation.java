package roomescape.domain.reservation;

import java.time.LocalDate;
import java.util.Objects;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.store.Store;
import roomescape.domain.reservation.theme.Theme;
import roomescape.domain.reservation.time.ReservationTime;

public class Reservation {

    private final Long id;
    private final LocalDate date;
    private final ReservationTime time;
    private final Theme theme;
    private final Member member;
    private final Store store;

    public Reservation(LocalDate date, ReservationTime time, Theme theme, Member member, Store store) {
        this(null, date, time, theme, member, store);
    }

    public Reservation(Long id, LocalDate date, ReservationTime time, Theme theme, Member member, Store store) {
        this.id = id;
        validate(date, time, theme, member, store);
        this.date = date;
        this.time = time;
        this.theme = theme;
        this.member = member;
        this.store = store;
    }

    private void validate(LocalDate date, ReservationTime time, Theme theme, Member member, Store store) {
        Objects.requireNonNull(date, "예약 날짜가 비어 있습니다.");
        Objects.requireNonNull(time, "시간이 비어 있습니다.");
        Objects.requireNonNull(theme, "테마가 비어 있습니다.");
        Objects.requireNonNull(member, "예약자가 비어 있습니다.");
        Objects.requireNonNull(store, "예약 매장이 비어 있습니다.");
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public ReservationTime getTime() {
        return time;
    }

    public Theme getTheme() {
        return theme;
    }

    public Member getMember() {
        return member;
    }

    public Store getStore() {
        return store;
    }
}
