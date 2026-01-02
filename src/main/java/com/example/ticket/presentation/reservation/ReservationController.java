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

    /**
     * Day4: 즉시 예약 생성 (eventId + seatId는 path, memberId는 body)
     */
    @PostMapping("/events/{eventId}/seats/{seatId}/reservations")
    public ResponseEntity<ReservationResponse> reserve(
            @PathVariable Long eventId,
            @PathVariable Long seatId,
            @RequestBody @Valid ReservationCreateRequest req
    ) {
        ReservationResponse res = reservationService.reserve(eventId, seatId, req.getMemberId());

        return ResponseEntity
                .created(URI.create("/api/reservations/" + res.getReservationId()))
                .body(res);
    }

    /**
     * Day5: 임시 예매(HOLD) 생성 - Postman 테스트용으로 가장 편한 형태
     * POST /api/reservations
     * Body: { "seatId": 10 }
     */
    @PostMapping("/reservations")
    public ResponseEntity<ReservationIdResponse> createHold(
            @RequestBody @Valid ReservationHoldCreateRequest req
    ) {
        Long reservationId = reservationService.hold(req.getSeatId());

        return ResponseEntity
                .created(URI.create("/api/reservations/" + reservationId))
                .body(new ReservationIdResponse(reservationId));
    }

    /**
     * 예약 단건 조회
     */
    @GetMapping("/reservations/{reservationId}")
    public ResponseEntity<ReservationResponse> get(@PathVariable Long reservationId) {
        return ResponseEntity.ok(reservationService.get(reservationId));
    }

    /**
     * Day4: 즉시 예약 취소 + 삭제
     */
    @DeleteMapping("/reservations/{reservationId}")
    public ResponseEntity<Void> cancelAndDelete(@PathVariable Long reservationId) {
        reservationService.cancelAndDelete(reservationId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Day5: 결제 성공(확정)
     */
    @PostMapping("/reservations/{reservationId}/confirm")
    public ResponseEntity<Void> confirm(@PathVariable Long reservationId) {
        reservationService.confirm(reservationId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Day5: 임시 예매 취소 (HOLD -> CANCELED)
     */
    @PostMapping("/reservations/{reservationId}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long reservationId) {
        reservationService.cancel(reservationId);
        return ResponseEntity.noContent().build();
    }
}

