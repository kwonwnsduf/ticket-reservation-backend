package com.example.ticket.presentation.reservation;

import com.example.ticket.application.reservation.ReservationService;
import com.example.ticket.presentation.reservation.dto.*;
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

    // 예매
    @PostMapping("/events/{eventId}/seats/{seatId}/reservations")
    public ResponseEntity<ReservationResponse> reserve(
            @PathVariable Long eventId,
            @PathVariable Long seatId,
            @RequestBody @Valid ReservationCreateRequest req
    ) {
        ReservationResponse res = reservationService.reserve(eventId, seatId, req.getMemberId());
        return ResponseEntity.created(
                URI.create("/api/reservations/" + res.getReservationId())
        ).body(res);
    }

    // 예매 조회
    @GetMapping("/reservations/{reservationId}")
    public ResponseEntity<ReservationResponse> get(@PathVariable Long reservationId) {
        return ResponseEntity.ok(reservationService.get(reservationId));
    }

    // 예매 취소
    @DeleteMapping("/reservations/{reservationId}")
    public ResponseEntity<Void> cancel(@PathVariable Long reservationId) {
        reservationService.cancel(reservationId);
        return ResponseEntity.noContent().build();
    }
}
