package roomescape.controller.admin;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.auth.resolver.LoginManager;
import roomescape.domain.member.manager.Manager;
import roomescape.dto.request.ReservationRequest;
import roomescape.dto.response.ReservationResponse;
import roomescape.service.ReservationService;

@RestController
@RequestMapping("/admin/reservations")
public class AdminReservationController {

    private final ReservationService reservationService;

    public AdminReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("my-store")
    public ResponseEntity<List<ReservationResponse>> myStoreReservation(@LoginManager Manager manager) {
        List<ReservationResponse> response = reservationService.findMyStoreReservations(manager);

        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationResponse> update(
            @LoginManager Manager manager,
            @PathVariable Long id,
            @Valid @RequestBody ReservationRequest request
    ) {
        ReservationResponse response = reservationService.updateDateTime(manager, id, request);

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @LoginManager Manager manager,
            @PathVariable Long id
    ) {
        reservationService.delete(manager, id);
        return ResponseEntity.noContent().build();
    }
}
