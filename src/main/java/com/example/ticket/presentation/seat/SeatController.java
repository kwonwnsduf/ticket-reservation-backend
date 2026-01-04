package com.example.ticket.presentation.seat;

import com.example.ticket.application.seat.SeatService;
import com.example.ticket.domain.seat.Seat;
import com.example.ticket.presentation.seat.dto.CreateSeatRequest;
import com.example.ticket.presentation.seat.dto.CreateSeatResponse;
import com.example.ticket.presentation.seat.dto.SeatResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events/{eventId}/seats")
public class SeatController {

    private final SeatService seatService;

    @PostMapping
    public ResponseEntity<CreateSeatResponse> create(@PathVariable Long eventId, @RequestBody @Valid CreateSeatRequest req) {
        Long seatId = seatService.create(eventId, req.getSeatNo());

        return ResponseEntity
                .created(URI.create("/api/events/" + eventId + "/seats/" + seatId)).body(new CreateSeatResponse(seatId));

    }

    @GetMapping
    public ResponseEntity<List<SeatResponse>> list(@PathVariable Long eventId) {
        List<Seat> seats = seatService.list(eventId);
        List<SeatResponse> res = seats.stream()
                .map(s -> new SeatResponse(s.getId(), s.getSeatNo(),s.isOccupied()))
                .toList();
        return ResponseEntity.ok(res);
    }

}
