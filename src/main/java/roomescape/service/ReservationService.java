package roomescape.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.common.exception.ConflictException;
import roomescape.common.exception.ForbiddenException;
import roomescape.common.exception.NotFoundException;
import roomescape.common.exception.UnprocessableEntityException;
import roomescape.dao.ManagerDao;
import roomescape.dao.MemberDao;
import roomescape.dao.ReservationDao;
import roomescape.dao.ReservationTimeDao;
import roomescape.dao.StoreDao;
import roomescape.dao.ThemeDao;
import roomescape.domain.member.Member;
import roomescape.domain.member.manager.Manager;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.store.Store;
import roomescape.domain.reservation.theme.Theme;
import roomescape.domain.reservation.time.ReservationTime;
import roomescape.dto.request.ReservationRequest;
import roomescape.dto.response.ReservationResponse;

@Service
public class ReservationService {
    private final ReservationDao reservationDao;
    private final ReservationTimeDao reservationTimeDao;
    private final ThemeDao themeDao;
    private final Clock clock;
    private final MemberDao memberDao;
    private final ManagerDao managerDao;
    private final StoreDao storeDao;

    public ReservationService(
            ReservationDao reservationDao,
            ReservationTimeDao reservationTimeDao,
            ThemeDao themeDao,
            Clock clock,
            MemberDao memberDao,
            ManagerDao managerDao,
            StoreDao storeDao
    ) {
        this.reservationDao = reservationDao;
        this.reservationTimeDao = reservationTimeDao;
        this.themeDao = themeDao;
        this.clock = clock;
        this.memberDao = memberDao;
        this.managerDao = managerDao;
        this.storeDao = storeDao;
    }

    public List<ReservationResponse> findMyStoreReservations(Long memberId) {
        Manager manager = managerDao.findByMemberId(memberId)
                .orElseThrow(() -> new ForbiddenException("권한이 없습니다."));

        Long storeId = manager.getStore().getId();

        List<Reservation> reservations = reservationDao.findAllByStoreId(storeId);

        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<ReservationResponse> findMyReservations(Long memberId) {
        List<Reservation> reservations = reservationDao.findAllByMemberId(memberId);

        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public ReservationResponse save(Long memberId, ReservationRequest request) {
        Reservation reservation = convertToReservation(null, request);

        Reservation saved = reservationDao.save(reservation);

        return ReservationResponse.from(saved);
    }

    public ReservationResponse updateDateTime(Long id, ReservationRequest request) {
        Reservation origin = reservationDao.findById(id)
                .orElseThrow(() -> new NotFoundException("변경하려는 예약이 존재하지 않습니다."));

        Member originMember = origin.getMember();

        if (!request.memberId().equals(originMember.getId())) {
            throw new ForbiddenException("다른 사람의 예약은 변경할 수 없습니다.");
        }

        Reservation modified = convertToReservation(id, request);

        boolean isSuccessful = reservationDao.update(modified);

        if (!isSuccessful) {
            throw new ConflictException("다른 사용자가 예약했습니다. 다시 시도해주세요.");
        }

        return ReservationResponse.from(modified);
    }

    private Reservation convertToReservation(Long id, ReservationRequest request) {
        ReservationTime time = reservationTimeDao.findTimeById(request.timeId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 시간입니다."));

        Theme theme = themeDao.findById(request.themeId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 테마입니다."));

        Member member = memberDao.findById(request.memberId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));

        Store store = storeDao.findById(request.storeId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 매장입니다."));

        validateAvailability(request.date(), time, theme);

        return new Reservation(
                id,
                request.date(),
                time,
                theme,
                member,
                store
        );
    }

    private void validateAvailability(LocalDate date, ReservationTime time, Theme theme) {
        validatePastTime(date, time);
        validateDuplicate(date, time, theme);
    }

    private void validatePastTime(LocalDate date, ReservationTime time) {
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime requestDateTime = LocalDateTime.of(date, time.getStartAt());
        if (requestDateTime.isBefore(now)) {
            throw new UnprocessableEntityException("이미 지난 시간입니다.");
        }
    }

    private void validateDuplicate(LocalDate date, ReservationTime time, Theme theme) {
        if (reservationDao.existsBy(date, theme, time)) {
            throw new ConflictException("이미 존재하는 예약 건입니다.");
        }
    }

    public void delete(Long id) {
        reservationDao.delete(id);
    }

    public void delete(Long id, Long memberId) {
        Reservation origin = reservationDao.findById(id)
                .orElseThrow(() -> new NotFoundException("삭제하려는 예약이 존재하지 않습니다."));

        if (!memberId.equals(origin.getMember().getId())) {
            throw new ForbiddenException("다른 사람의 예약은 삭제할 수 없습니다.");
        }

        validatePastTime(origin.getDate(), origin.getTime());

        reservationDao.delete(id);
    }
}
