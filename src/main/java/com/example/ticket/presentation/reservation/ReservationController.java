package com.example.ticket.presentation.reservation;
import com.example.ticket.application.reservation.ReservationService;
import com.example.ticket.presentation.reservation.dto.HoldResponse;
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
    @PostMapping("/events/{eventId}/seats/{seatId}/hold")
    public ResponseEntity<HoldResponse> hold(
            @PathVariable Long eventId,
            @PathVariable Long seatId,
            @RequestBody @Valid ReservationCreateRequest req
    ) {
        HoldResponse res = reservationService.create(eventId, seatId, req.getMemberId());
        return ResponseEntity.ok(res);
    }

    /** Day13 고급: 확정 = Redis HOLD 검증 후 DB CONFIRMED 저장 */
//    @PostMapping("/events/{eventId}/seats/{seatId}/confirm")
//    public ResponseEntity<ReservationResponse> confirm(
//            @PathVariable Long eventId,
//            @PathVariable Long seatId,
//            @RequestBody @Valid ReservationCreateRequest req
//    ) {
//        ReservationResponse res = reservationService.confirm(eventId, seatId, req.getMemberId());
//        return ResponseEntity.ok(res);
//    }

    @GetMapping("/reservations/{reservationId}")
    public ResponseEntity<ReservationResponse> get(@PathVariable Long reservationId) {
        return ResponseEntity.ok(reservationService.get(reservationId));
    }

    /** Day13 고급: HOLD 취소 */
    @PostMapping("/events/{eventId}/seats/{seatId}/hold/cancel")
    public ResponseEntity<Void> cancelHold(
            @PathVariable Long eventId,
            @PathVariable Long seatId,
            @RequestBody @Valid ReservationCreateRequest req
    ) {
        reservationService.cancelHold(eventId, seatId, req.getMemberId());
        return ResponseEntity.noContent().build();
    }

    /** (선택) 확정 취소(DB 기록 남김) */
    @PostMapping("/reservations/{reservationId}/cancel")
    public ResponseEntity<Void> cancelConfirmed(@PathVariable Long reservationId) {
        reservationService.cancelConfirmed(reservationId);
        return ResponseEntity.noContent().build();
    }

}

