package com.example.ticket.application.reservation;

import com.example.ticket.application.event.EventService;
import com.example.ticket.domain.event.Event;
import com.example.ticket.domain.reservation.Reservation;
import com.example.ticket.domain.reservation.ReservationRepository;
import com.example.ticket.domain.seat.Seat;
import com.example.ticket.domain.seat.SeatRepository;
import com.example.ticket.global.exception.ApiException;
import com.example.ticket.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final EventService eventService;

    public Long reserve(Long eventId, Long seatId, Long memberId) {
        Event event = eventService.get(eventId);
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

        // 같은 event + seat은 1번만 예약되게
        reservationRepository.findByEventIdAndSeatId(eventId, seatId)
                .ifPresent(r -> { throw new ApiException(ErrorCode.ALREADY_RESERVED); });

        Reservation reservation = Reservation.builder()
                .event(event)
                .seat(seat)
                .memberId(memberId)
                .reservedAt(LocalDateTime.now())
                .build();

        return reservationRepository.save(reservation).getId();
    }
}

