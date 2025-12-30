package com.example.ticket.presentation.reservation;

import com.example.ticket.application.reservation.ReservationService;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events/{eventId}/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReserveResponse> reserve(@PathVariable Long eventId, @RequestBody ReserveRequest req) {
        Long id = reservationService.reserve(eventId, req.seatId, req.memberId);

        return ResponseEntity
                .created(URI.create("/api/events/" + eventId + "/reservations/" + id))
                .body(new ReserveResponse(id));
    }

    @Getter @NoArgsConstructor
    static class ReserveRequest {
        @NotNull public Long seatId;
        @NotNull public Long memberId;
    }

    @Getter @AllArgsConstructor
    static class ReserveResponse {
        private Long id;
    }
}
