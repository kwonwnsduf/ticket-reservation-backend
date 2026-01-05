package com.example.ticket.presentation.reservation;
import com.example.ticket.application.reservation.ReservationService;
import com.example.ticket.presentation.reservation.dto.ReservationCreateRequest;
import com.example.ticket.presentation.reservation.dto.ReservationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReservationController {

    private final ReservationService reservationService;

    // Day6: 예매 생성(좌석 HOLD + Reservation PENDING)
    @PostMapping("/events/{eventId}/seats/{seatId}/reservations")
    public ResponseEntity<ReservationResponse> create(
            @PathVariable Long eventId,
            @PathVariable Long seatId,
            @RequestBody @Valid ReservationCreateRequest req
    ) {
        ReservationResponse res = reservationService.create(eventId, seatId, req.getMemberId());
        return ResponseEntity
                .created(URI.create("/api/reservations/" + res.getReservationId()))
                .body(res);
    }

    @GetMapping("/reservations/{reservationId}")
    public ResponseEntity<ReservationResponse> get(@PathVariable Long reservationId) {
        return ResponseEntity.ok(reservationService.get(reservationId));
    }


    @PostMapping("/reservations/{reservationId}/cancel")
    public ResponseEntity<Void> delete(@PathVariable Long reservationId) {
        reservationService.cancel(reservationId);
        return ResponseEntity.noContent().build();
    }
}

