package roomescape.dto.response;

import java.time.LocalDate;
import roomescape.domain.reservation.Reservation;

public record ReservationResponse(
        Long id,
        LocalDate date,
        ReservationTimeResponse timeResponse,
        ThemeResponse themeResponse,
        MemberResponse memberResponse
) {

    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getDate(),
                ReservationTimeResponse.from(reservation.getTime()),
                ThemeResponse.from(reservation.getTheme()),
                MemberResponse.from(reservation.getMember())
        );
    }
}
