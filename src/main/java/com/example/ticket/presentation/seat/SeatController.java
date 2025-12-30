package com.example.ticket.presentation.seat;

import com.example.ticket.application.seat.SeatService;
import com.example.ticket.domain.seat.Seat;
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
    public ResponseEntity<CreateSeatResponse> create(@PathVariable Long eventId, @RequestBody CreateSeatRequest req) {
        Long id = seatService.create(eventId, req.seatNo);

        return ResponseEntity
                .created(URI.create("/api/events/" + eventId + "/seats/" + id))
                .body(new CreateSeatResponse(id));
    }

    @GetMapping
    public ResponseEntity<List<SeatResponse>> list(@PathVariable Long eventId) {
        List<Seat> seats = seatService.list(eventId);
        List<SeatResponse> res = seats.stream()
                .map(s -> new SeatResponse(s.getId(), s.getSeatNo()))
                .toList();
        return ResponseEntity.ok(res);
    }

    @Getter @NoArgsConstructor
    static class CreateSeatRequest {
        @NotBlank public String seatNo;
    }

    @Getter @AllArgsConstructor
    static class CreateSeatResponse {
        private Long id;
    }

    @Getter @AllArgsConstructor
    static class SeatResponse {
        private Long id;
        private String seatNo;
    }
}
